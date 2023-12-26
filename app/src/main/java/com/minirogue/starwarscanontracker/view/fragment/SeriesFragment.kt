package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.core.model.room.entity.SeriesDto
import com.minirogue.starwarscanontracker.databinding.FragmentSeriesBinding
import com.minirogue.starwarscanontracker.view.adapter.SeriesListAdapter
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeriesFragment : Fragment() {

    private val viewModel: SeriesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentSeriesBinding.inflate(inflater, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(getString(R.string.bundleItemId), -1) ?: -1
        if (bundleItemId != -1) viewModel.setSeriesId(bundleItemId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.liveSeriesDto.asFlow()
                .combine(viewModel.isNetworkAllowed) { series, isNetworkAllowed -> Pair(series, isNetworkAllowed) }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { updateViews(it.first, it.second, fragmentBinding) }
        }
        viewModel.liveSeriesNotes.observe(viewLifecycleOwner) { notes -> updateViews(notes, fragmentBinding) }
        viewModel.checkBoxNames.asLiveData(lifecycleScope.coroutineContext).observe(
            viewLifecycleOwner
        ) { names -> setCheckBoxNames(names, fragmentBinding) }
        viewModel.checkBoxVisibility.asLiveData(lifecycleScope.coroutineContext).observe(
            viewLifecycleOwner
        ) { visibility -> setCheckBoxVisibility(visibility, fragmentBinding) }
        fragmentBinding.checkbox3.setOnClickListener { viewModel.toggleCheckbox3() }
        fragmentBinding.checkbox2.setOnClickListener { viewModel.toggleCheckbox2() }
        fragmentBinding.checkbox1.setOnClickListener { viewModel.toggleCheckbox1() }

        val recyclerView = fragmentBinding.seriesRecyclerview
        val adapter = SeriesListAdapter { itemId ->
            val viewMediaItemFragment = ViewMediaItemFragment()
            val newBundle = Bundle()
            newBundle.putInt(getString(R.string.bundleItemId), itemId)
            viewMediaItemFragment.arguments = newBundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, viewMediaItemFragment)
                .addToBackStack(null)
                .commit() // TODO this should be handled by the activity
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        viewModel.seriesList.observe(viewLifecycleOwner) { adapter.submitList(it) }

        return fragmentBinding.root
    }

    private fun setCheckBoxVisibility(visibility: BooleanArray, fragmentBinding: FragmentSeriesBinding) {
        fragmentBinding.checkbox1.visibility = if (visibility[0]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox2.visibility = if (visibility[1]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox3.visibility = if (visibility[2]) View.VISIBLE else View.GONE
    }

    private fun setCheckBoxNames(names: Array<String>, fragmentBinding: FragmentSeriesBinding) {
        fragmentBinding.checkbox1.text = names[0]
        fragmentBinding.checkbox2.text = names[1]
        fragmentBinding.checkbox3.text = names[2]
    }

    private fun updateViews(seriesDto: SeriesDto, isNetworkAllowed: Boolean, fragmentBinding: FragmentSeriesBinding) {
        fragmentBinding.seriesTitle.text = seriesDto.title

        fragmentBinding.seriesImage.load(seriesDto.imageURL) {
            placeholder(R.drawable.ic_launcher_foreground)
            if (isNetworkAllowed) {
                networkCachePolicy(CachePolicy.ENABLED)
            } else networkCachePolicy(CachePolicy.DISABLED)
        }
    }

    private fun updateViews(notes: Array<Boolean>, fragmentBinding: FragmentSeriesBinding) {
        fragmentBinding.checkbox1.isChecked = notes[0]
        fragmentBinding.checkbox2.isChecked = notes[1]
        fragmentBinding.checkbox3.isChecked = notes[2]
    }
}
