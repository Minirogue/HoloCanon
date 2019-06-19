package com.minirogue.starwarscanontracker

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.database.MediaItem
import com.minirogue.starwarscanontracker.database.MediaNotes
import kotlinx.android.synthetic.main.fragment_view_media_item.view.*




class ViewMediaItemFragment : Fragment(){

    private val MENU_ITEM_AMAZON_BUY = 1
    private val MENU_ITEM_AMAZON_STREAM = 2
    private var viewModel: ViewMediaItemViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_view_media_item, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        viewModel = ViewModelProviders.of(this, ViewMediaItemViewModelFactory(activity!!.application, bundleItemId)).get(ViewMediaItemViewModel::class.java)
        viewModel?.let {viewModel ->
            viewModel.liveMediaItem.observe(this, Observer { item -> updateViews(item, fragmentView) })
            viewModel.liveMediaNotes.observe(this, Observer { notes -> updateViews(notes, fragmentView) })
            fragmentView.text_watched_or_read.text = viewModel.getCheckBoxText(1)
            fragmentView.text_want_to_watch_or_read.text = viewModel.getCheckBoxText(2)
            fragmentView.text_owned.text = viewModel.getCheckBoxText(3)
            fragmentView.checkbox_owned.setOnClickListener { viewModel.toggleOwned() }
            fragmentView.checkbox_want_to_watch_or_read.setOnClickListener { viewModel.toggleWantToWatchRead() }
            fragmentView.checkbox_watched_or_read.setOnClickListener { viewModel.toggleWatchedRead() }
        }
        return fragmentView
    }

    private fun updateViews(item : MediaItem, fragmentView: View){
        fragmentView.media_title.text = item.title
        fragmentView.media_type.text = FilterObject.getTextForType(item.type)
        fragmentView.description_textview.text = item.description
        fragmentView.release_date.text = item.date
        fragmentView.image_cover.hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE)
        val request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(item.imageURL))
                .setLowestPermittedRequestLevel(if (viewModel!!.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                .build()
        fragmentView.image_cover.setImageRequest(request)
        fragmentView.image_cover.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
        makeShoppingMenu(item, fragmentView)
    }

    private fun updateViews(notes : MediaNotes, fragmentView: View){
        fragmentView.checkbox_owned.isChecked = notes.isOwned
        fragmentView.checkbox_watched_or_read.isChecked = notes.isWatchedRead
        fragmentView.checkbox_want_to_watch_or_read.isChecked = notes.isWantToWatchRead
    }

    private fun makeShoppingMenu(item: MediaItem, fragView: View) {
        val shoppingMenu = PopupMenu(fragView.context, fragView.affiliate_links_fab)
        if (item.amazonLink != null && item.amazonLink != "") {
            fragView.affiliate_links_fab.show()
            shoppingMenu.menu.add(0, MENU_ITEM_AMAZON_BUY, 0, "Buy on Amazon")
        }
        if (item.amazonStream != null && item.amazonStream != ""){
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


    internal inner class ViewMediaItemViewModelFactory(val application: Application, private val itemId: Int) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewMediaItemViewModel(application, itemId) as T
        }
    }
}