package com.minirogue.starwarscanontracker.view.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication
import com.minirogue.starwarscanontracker.model.room.entity.Series
import com.minirogue.starwarscanontracker.view.adapter.SeriesListAdapter
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import kotlinx.android.synthetic.main.fragment_series.view.*
import javax.inject.Inject


class SeriesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SeriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_series, container, false)
        (activity!!.application as CanonTrackerApplication).appComponent.inject(this)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SeriesViewModel::class.java)
        if (bundleItemId != -1) viewModel.setSeriesId(bundleItemId)
        viewModel.liveSeries.observe(viewLifecycleOwner, Observer { series -> updateViews(series, fragmentView) })
        viewModel.liveSeriesNotes.observe(viewLifecycleOwner, Observer { notes -> updateViews(notes, fragmentView) })
        viewModel.checkBoxNames.observe(viewLifecycleOwner, Observer { names -> setCheckBoxNames(names, fragmentView) })
        viewModel.checkBoxVisibility.observe(viewLifecycleOwner, Observer { visibility -> setCheckBoxVisibility(visibility, fragmentView) })
        fragmentView.checkbox_3.setOnClickListener { viewModel.toggleCheckbox3() }
        fragmentView.checkbox_2.setOnClickListener { viewModel.toggleCheckbox2() }
        fragmentView.checkbox_1.setOnClickListener { viewModel.toggleCheckbox1() }


        val recyclerView = fragmentView.series_recyclerview
        val adapter = SeriesListAdapter(SeriesListAdapter.OnItemClickedListener { itemId ->
            val viewMediaItemFragment = ViewMediaItemFragment()
            val newBundle = Bundle()
            newBundle.putInt(getString(R.string.bundleItemId), itemId)
            viewMediaItemFragment.arguments = newBundle
            activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, viewMediaItemFragment)
                    .addToBackStack(null)
                    .commit() //TODO this should be handled by the activity
        })
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        viewModel.seriesList.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })


        return fragmentView
    }

    private fun setCheckBoxVisibility(visibility: BooleanArray, fragmentView: View){
        fragmentView.checkbox_1.visibility = if (visibility[0]) View.VISIBLE else View.GONE
        fragmentView.checkbox_2.visibility = if (visibility[1]) View.VISIBLE else View.GONE
        fragmentView.checkbox_3.visibility = if (visibility[2]) View.VISIBLE else View.GONE
    }

    private fun setCheckBoxNames(names: Array<String>, fragmentView: View) {
        fragmentView.checkbox_1.text = names[0]
        fragmentView.checkbox_2.text = names[1]
        fragmentView.checkbox_3.text = names[2]
    }

    private fun updateViews(series: Series, fragmentView: View) {
        fragmentView.series_title.text = series.title
        //fragmentView.description_textview.text = series.description
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

}
