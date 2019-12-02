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
import com.minirogue.starwarscanontracker.model.room.entity.Series
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import kotlinx.android.synthetic.main.fragment_series.view.*


class SeriesFragment : Fragment() {

    private lateinit var viewModel: SeriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_series, container, false)
        setTextBoxes(fragmentView, activity!!.application)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        viewModel = ViewModelProviders.of(this, SeriesViewModelFactory(bundleItemId)).get(SeriesViewModel::class.java)
        viewModel.liveSeries.observe(viewLifecycleOwner, Observer { series -> updateViews(series, fragmentView) })
        viewModel.liveSeriesNotes.observe(viewLifecycleOwner, Observer { notes -> updateViews(notes, fragmentView) })
        fragmentView.checkbox_3.setOnClickListener { viewModel.toggleOwned() }
        fragmentView.checkbox_2.setOnClickListener { viewModel.toggleWantToWatchRead() }
        fragmentView.checkbox_1.setOnClickListener { viewModel.toggleWatchedRead() }

        return fragmentView

    }

    private fun setTextBoxes(fragmentView: View, application: Application) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        fragmentView.text_checkbox_1.text = prefs.getString(application.getString(R.string.checkbox1_default_text), application.getString(R.string.checkbox1_default_text))
                ?: ""
        fragmentView.text_checkbox_2.text = prefs.getString(application.getString(R.string.checkbox2_default_text), application.getString(R.string.checkbox2_default_text))
                ?: ""
        fragmentView.text_checkbox_3.text = prefs.getString(application.getString(R.string.checkbox3_default_text), application.getString(R.string.checkbox3_default_text))
                ?: ""
    }

    private fun updateViews(series: Series, fragmentView: View) {
        fragmentView.series_title.text = series.title
        fragmentView.description_textview.text = series.description
        fragmentView.series_image.hierarchy.setPlaceholderImage(R.drawable.ic_launcher_foreground, ScalingUtils.ScaleType.CENTER_INSIDE)
        val request = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(series.imageURL))
                .setLowestPermittedRequestLevel(if (viewModel.isNetworkAllowed()) ImageRequest.RequestLevel.FULL_FETCH else ImageRequest.RequestLevel.DISK_CACHE)
                .build()
        fragmentView.series_image.setImageRequest(request)
        fragmentView.series_image.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
    }

    private fun updateViews(notes: Array<Boolean>, fragmentView: View) {
        fragmentView.checkbox_1.isChecked = notes[0]
        fragmentView.checkbox_2.isChecked = notes[1]
        fragmentView.checkbox_3.isChecked = notes[2]
    }


    internal inner class SeriesViewModelFactory(private val itemId: Int) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SeriesViewModel(itemId) as T
        }
    }

}
