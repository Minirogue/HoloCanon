package com.minirogue.holocanon.feature.series.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.CachePolicy
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import com.minirogue.holocanon.feature.series.internal.R
import com.minirogue.holocanon.feature.series.internal.databinding.SeriesFragmentBinding
import com.minirogue.series.model.Series
import com.minirogue.starwarscanontracker.core.nav.NavigationDestination
import com.minirogue.starwarscanontracker.core.nav.NavigationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class SeriesFragment : Fragment() {
    @Inject
    lateinit var getMediaItemFragment: GetMediaItemFragment

    private val viewModel: SeriesViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = SeriesFragmentBinding.inflate(inflater, container, false)
        val bundle = this.arguments
        val bundleItemId = bundle?.getInt(SERIES_ID_BUNDLE_KEY, -1) ?: -1
        if (bundleItemId != -1) viewModel.setSeriesId(bundleItemId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.seriesFlow
                .combine(viewModel.isNetworkAllowed) { series, isNetworkAllowed ->
                    Pair(
                        series,
                        isNetworkAllowed
                    )
                }
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { updateViews(it.first, it.second, fragmentBinding) }
        }
        viewModel.liveSeriesNotes.observe(viewLifecycleOwner) { notes ->
            updateViews(
                notes,
                fragmentBinding
            )
        }
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
            navigationViewModel.navigateTo(NavigationDestination.MediaItemScreen(itemId))
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        viewModel.seriesList.observe(viewLifecycleOwner) { adapter.submitList(it) }

        return fragmentBinding.root
    }

    private fun setCheckBoxVisibility(
        visibility: BooleanArray,
        fragmentBinding: SeriesFragmentBinding
    ) {
        fragmentBinding.checkbox1.visibility = if (visibility[0]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox2.visibility = if (visibility[1]) View.VISIBLE else View.GONE
        fragmentBinding.checkbox3.visibility = if (visibility[2]) View.VISIBLE else View.GONE
    }

    private fun setCheckBoxNames(names: Array<String>, fragmentBinding: SeriesFragmentBinding) {
        fragmentBinding.checkbox1.text = names[0]
        fragmentBinding.checkbox2.text = names[1]
        fragmentBinding.checkbox3.text = names[2]
    }

    private fun updateViews(
        series: Series,
        isNetworkAllowed: Boolean,
        fragmentBinding: SeriesFragmentBinding
    ) {
        fragmentBinding.seriesTitle.text = series.name

        fragmentBinding.seriesImage.load(series.imageUrl) {
            placeholder(R.drawable.common_resources_app_icon)
            if (isNetworkAllowed) {
                networkCachePolicy(CachePolicy.ENABLED)
            } else networkCachePolicy(CachePolicy.DISABLED)
        }
    }

    private fun updateViews(notes: Array<Boolean>, fragmentBinding: SeriesFragmentBinding) {
        fragmentBinding.checkbox1.isChecked = notes[0]
        fragmentBinding.checkbox2.isChecked = notes[1]
        fragmentBinding.checkbox3.isChecked = notes[2]
    }

    companion object {
        const val SERIES_ID_BUNDLE_KEY = "series-id"
    }
}
