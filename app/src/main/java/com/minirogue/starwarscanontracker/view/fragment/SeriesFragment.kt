package com.minirogue.starwarscanontracker.view.fragment

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import com.minirogue.starwarscanontracker.model.room.entity.Series
import kotlinx.android.synthetic.main.fragment_series.view.*


class SeriesFragment : Fragment() {

    companion object {
        fun newInstance() = SeriesFragment()
    }

    private lateinit var viewModel: SeriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_series, container, false)
        setTextBoxes(fragmentView, activity!!.application)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        viewModel = ViewModelProviders.of(this, SeriesViewModelFactory(activity!!.application, bundleItemId)).get(SeriesViewModel::class.java)
        viewModel?.let {viewModel ->
            viewModel.liveSeries.observe(viewLifecycleOwner, Observer { series -> updateViews(series, fragmentView) })
            viewModel.liveSeriesNotes.observe(viewLifecycleOwner, Observer { notes -> updateViews(notes, fragmentView) })
            fragmentView.checkbox_owned.setOnClickListener { viewModel.toggleOwned() }
            fragmentView.checkbox_want_to_watch_or_read.setOnClickListener { viewModel.toggleWantToWatchRead() }
            fragmentView.checkbox_watched_or_read.setOnClickListener { viewModel.toggleWatchedRead() }
        }
        return fragmentView

    }

    fun setTextBoxes(fragmentView: View, application: Application){
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        fragmentView.text_watched_or_read.text = prefs.getString(application.getString(R.string.checkbox1_default_text), application.getString(R.string.checkbox1_default_text))?: ""
        fragmentView.text_want_to_watch_or_read.text = prefs.getString(application.getString(R.string.checkbox2_default_text), application.getString(R.string.checkbox2_default_text))?: ""
        fragmentView.text_owned.text = prefs.getString(application.getString(R.string.checkbox3_default_text), application.getString(R.string.checkbox3_default_text))?: ""
    }

    fun updateViews(series: Series, fragmentView: View){
        fragmentView.series_title.text = series.title
        fragmentView.description_textview.text = series.description
        fragmentView.series_image.hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE)
        val request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(series.imageURL))
                .setLowestPermittedRequestLevel(if (viewModel!!.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                .build()
        fragmentView.series_image.setImageRequest(request)
        fragmentView.series_image.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
    }
    fun updateViews(notes: Array<Boolean>, fragmentView: View){
        fragmentView.checkbox_watched_or_read.isChecked = notes[0]
        fragmentView.checkbox_want_to_watch_or_read.isChecked = notes[1]
        fragmentView.checkbox_owned.isChecked = notes[2]
    }


    internal inner class SeriesViewModelFactory(val application: Application, private val itemId: Int) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SeriesViewModel(application, itemId) as T
        }
    }

}
