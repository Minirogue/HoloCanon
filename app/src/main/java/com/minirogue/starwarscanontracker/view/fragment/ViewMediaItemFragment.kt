package com.minirogue.starwarscanontracker.view.fragment

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
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
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
        viewModel.checkBoxText.observe(viewLifecycleOwner, Observer { arr ->
            fragmentView.text_checkbox_1.text = arr[0]
            fragmentView.text_checkbox_2.text = arr[1]
            fragmentView.text_checkbox_3.text = arr[2]
        })

        fragmentView.checkbox_3.setOnClickListener { viewModel.toggleOwned() }
        fragmentView.checkbox_2.setOnClickListener { viewModel.toggleWantToWatchRead() }
        fragmentView.checkbox_1.setOnClickListener { viewModel.toggleWatchedRead() }

        return fragmentView
    }

    private fun updateViews(item: MediaItem, fragmentView: View) {
        fragmentView.media_title.text = item.title
        fragmentView.media_type.text = FilterObject.getTextForType(item.type)
        fragmentView.description_textview.text = getString(R.string.description_header) + item.description
        fragmentView.review_textview.text = getString(R.string.review_header) + item.review
        fragmentView.release_date.text = item.date
        fragmentView.image_cover.hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE)
        val request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(item.imageURL))
                .setLowestPermittedRequestLevel(if (viewModel.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                .build()
        fragmentView.image_cover.setImageRequest(request)
        fragmentView.image_cover.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
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
        fragmentView.checkbox_3.isChecked = notes.isOwned
        fragmentView.checkbox_1.isChecked = notes.isWatchedRead
        fragmentView.checkbox_2.isChecked = notes.isWantToWatchRead
    }

    private fun makeShoppingMenu(item: MediaItem, fragView: View) {
        val shoppingMenu = PopupMenu(fragView.context, fragView.affiliate_links_fab)
        if (item.amazonLink != null && item.amazonLink != "") {
            fragView.affiliate_links_fab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_BUY, 0, "Buy on Amazon")
        }
        if (item.amazonStream != null && item.amazonStream != "") {
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