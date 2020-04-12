package com.minirogue.starwarscanontracker.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.entity.MediaType
import com.minirogue.starwarscanontracker.viewmodel.ViewMediaItemViewModel
import kotlinx.android.synthetic.main.fragment_view_media_item.view.*
import javax.inject.Inject


class ViewMediaItemFragment : Fragment() {

    companion object {
        private const val MENU_ITEM_AMAZON_BUY = 1
        private const val MENU_ITEM_AMAZON_STREAM = 2
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ViewMediaItemViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_view_media_item, container, false)
        (activity!!.application as CanonTrackerApplication).appComponent.inject(this)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ViewMediaItemViewModel::class.java)
        if (bundleItemId != -1) viewModel.setItemId(bundleItemId)
        viewModel.liveMediaItem.observe(viewLifecycleOwner, Observer { item -> updateViews(item, fragmentView) })
        viewModel.liveMediaNotes.observe(viewLifecycleOwner, Observer { notes -> updateViews(notes, fragmentView) })
        viewModel.liveMediaType.observe(viewLifecycleOwner, Observer { mediaType -> updateView(mediaType, fragmentView) })
        viewModel.checkBoxText.observe(viewLifecycleOwner, Observer { arr ->
            fragmentView.checkbox_1.text = arr[0]
            fragmentView.checkbox_2.text = arr[1]
            fragmentView.checkbox_3.text = arr[2]
        })
        viewModel.checkBoxVisibility.observe(viewLifecycleOwner, Observer { visibilityArray -> updateViews(visibilityArray, fragmentView)})

        fragmentView.checkbox_3.setOnClickListener { viewModel.toggleCheckbox3() }
        fragmentView.checkbox_2.setOnClickListener { viewModel.toggleCheckbox2() }
        fragmentView.checkbox_1.setOnClickListener { viewModel.toggleCheckbox1() }

        return fragmentView
    }

    private fun updateViews(visibilityArray: BooleanArray, fragmentView: View){
        fragmentView.checkbox_1.visibility = if (visibilityArray[0]) View.VISIBLE else View.GONE
        fragmentView.checkbox_2.visibility = if (visibilityArray[1]) View.VISIBLE else View.GONE
        fragmentView.checkbox_3.visibility = if (visibilityArray[2]) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun updateViews(item: MediaItem, fragmentView: View) {
        fragmentView.media_title.text = item.title
        fragmentView.description_textview.text = if (item.description.isNotBlank()) getString(R.string.description_header) + " " + item.description else ""
        //fragmentView.review_textview.text = getString(R.string.review_header) + " " + item.review
        fragmentView.release_date.text = item.date
        fragmentView.image_cover.hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.FIT_CENTER)
        val request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(item.imageURL))
                .setLowestPermittedRequestLevel(if (viewModel.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                .build()
        fragmentView.image_cover.setImageRequest(request)
        fragmentView.image_cover.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        makeShoppingMenu(item, fragmentView)
        if (item.series > 0) {
            fragmentView.view_series_button.visibility = View.VISIBLE
            fragmentView.view_series_button.setOnClickListener {
                val seriesFragment = SeriesFragment()
                val bundle = Bundle()
                bundle.putInt(getString(R.string.bundleItemId), item.series)
                seriesFragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, seriesFragment)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    private fun updateViews(notes: MediaNotes, fragmentView: View) {
        fragmentView.checkbox_3.isChecked = notes.isBox3Checked
        fragmentView.checkbox_1.isChecked = notes.isBox1Checked
        fragmentView.checkbox_2.isChecked = notes.isBox2Checked
    }

    private fun updateView(mediaType: MediaType?, fragmentView: View) {
        fragmentView.media_type.text = mediaType?.text ?: ""
    }

    private fun makeShoppingMenu(item: MediaItem, fragView: View) {
        val shoppingMenu = PopupMenu(fragView.context, fragView.affiliate_links_fab)
        if (item.amazonLink != "") {
            fragView.affiliate_links_fab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_BUY, 0, "Buy on Amazon")
        }
        if (item.amazonStream != "") {
            fragView.affiliate_links_fab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_STREAM, 0, "Stream on Amazon Video")
        }
        shoppingMenu.setOnMenuItemClickListener { menuItem ->
            val url = when (menuItem.itemId) {
                MENU_ITEM_AMAZON_BUY -> item.amazonLink
                MENU_ITEM_AMAZON_STREAM -> item.amazonStream
                else -> ""
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
            true
        }
        fragView.affiliate_links_fab.setOnClickListener { shoppingMenu.show() }
    }


    /*internal inner class ViewMediaItemViewModelFactory(private val itemId: Int, private val application: CanonTrackerApplication) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewMediaItemViewModel(itemId, application) as T
        }
    }*/
}