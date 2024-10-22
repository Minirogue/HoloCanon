package com.minirogue.holocanon.feature.select.filters.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.minirogue.holocanon.feature.select.filters.internal.databinding.SelectFiltersFragmentBinding
import com.minirogue.starwarscanontracker.view.FilterChip
import dagger.hilt.android.AndroidEntryPoint
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterSelectionFragment : Fragment() {

    private val viewModel: FilterSelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = SelectFiltersFragmentBinding.inflate(inflater, container, false)
        val fragmentContext = fragmentBinding.root.context

        val recyclerView = fragmentBinding.generalRecyclerview

        val activeChipGroup = fragmentBinding.selectedChipgroup

        viewModel.getActiveFilters().asLiveData(lifecycleScope.coroutineContext).observe(viewLifecycleOwner) { activeFilters ->
            activeChipGroup.removeAllViews(); activeFilters.forEach {
            activeChipGroup.addView(makeCurrentFilterChip(it))
        }
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(fragmentContext)
        val adapter = FilterSelectionAdapter()
        adapter.setOnClickListeners(object : FilterSelectionAdapter.OnClickListeners {
            override fun onFilterClicked(mediaFilter: MediaFilter) {
                viewModel.flipFilterActive(mediaFilter)
            }

            override fun setFilterGroupObservation(chipGroup: ChipGroup, filterGroup: FilterGroup) {
                viewModel.getFiltersOfType(filterGroup.type).asLiveData(lifecycleScope.coroutineContext)
                    .observe(viewLifecycleOwner) { filters ->
                        chipGroup.removeAllViews()
                        filters.forEach { mediaFilter ->
                            if (!mediaFilter.isActive) {
                                val filterChip = FilterChip(mediaFilter, fragmentContext)
                                filterChip.setOnClickListener {
                                    viewModel.flipFilterActive(mediaFilter)
                                }
                                filterChip.setOnCloseIconClickListener {
                                    viewModel.setFilterInactive(mediaFilter)
                                }
                                chipGroup.addView(filterChip)
                            }
                        }
                    }
            }

            override fun onFilterTypeSwitchClicked(filterGroup: FilterGroup) {
                viewModel.flipFilterType(filterGroup)
            }
        })
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = mLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
               launch {
                   viewModel.checkboxSettings.collect { checkboxSettings ->
                       val list = ArrayList<FilterType>()
                       if (!checkboxSettings.checkbox1Setting.isInUse) {
                           list.add(FilterType.CheckboxOne)
                       }
                       if (!checkboxSettings.checkbox2Setting.isInUse) {
                           list.add(FilterType.CheckboxTwo)
                       }
                       if (!checkboxSettings.checkbox3Setting.isInUse) {
                           list.add(FilterType.CheckboxThree)
                       }
                       adapter.updateExcludedTypes(list)
                   }
               }
                launch {
                    viewModel.filterTypes.collect { filterTypes -> adapter.updateList(filterTypes) }
                }
            }
        }

        return fragmentBinding.root
    }

    private fun makeCurrentFilterChip(mediaFilter: MediaFilter): Chip {
        val filterChip: Chip = FilterChip(mediaFilter, requireView().context)
        filterChip.setOnCloseIconClickListener { viewModel.deactivateFilter(mediaFilter) }
        return filterChip
    }
}
