package com.minirogue.starwarscanontracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModel
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.database.MediaItem
import com.minirogue.starwarscanontracker.database.MediaNotes
import kotlinx.android.synthetic.main.fragment_view_media_item.view.*
import java.lang.ref.WeakReference


class ViewMediaItemFragment(private val itemId: Int) : Fragment(){


    private var viewModel: ViewMediaItemViewModel? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, ViewMediaItemViewModelFactory(activity!!.application, itemId)).get(ViewMediaItemViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_view_media_item, container, false)
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
                .setLowestPermittedRequestLevel(if (viewModel!!.isNetworkMetered()) ImageRequest.RequestLevel.DISK_CACHE else ImageRequest.RequestLevel.FULL_FETCH)
                .build()
        fragmentView.image_cover.setImageRequest(request)
    }

    private fun updateViews(notes : MediaNotes, fragmentView: View){
        fragmentView.checkbox_owned.isChecked = notes.isOwned
        fragmentView.checkbox_watched_or_read.isChecked = notes.isWatchedRead
        fragmentView.checkbox_want_to_watch_or_read.isChecked = notes.isWantToWatchRead
    }


    internal inner class ViewMediaItemViewModelFactory(val application: Application, private val itemId: Int) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewMediaItemViewModel(application, itemId) as T
        }
    }
}