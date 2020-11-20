package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.adapter.FilterSelectionAdapter
import com.minirogue.starwarscanontracker.viewmodel.FilterSelectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_filter_selection.view.*

@AndroidEntryPoint
class FilterSelectionFragment : Fragment() {

    private val viewModel: FilterSelectionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_filter_selection, container, false)
        val recyclerView = fragmentView.general_recyclerview

        val activeChipGroup = fragmentView.selected_chipgroup

        viewModel.getActiveFilters().observe(viewLifecycleOwner,
                { activeFilters ->
                    activeChipGroup.removeAllViews(); activeFilters.forEach {
                    activeChipGroup.addView(makeCurrentFilterChip(it))
                }
                })

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(fragmentView.context)
        val adapter = FilterSelectionAdapter()
        adapter.setOnClickListeners(object : FilterSelectionAdapter.OnClickListeners {
            override fun onFilterClicked(filterObject: FilterObject) {
                viewModel.flipFilterActive(filterObject)
            }

            override fun setFilterGroupObservation(chipGroup: ChipGroup, filterType: FilterType) {
                viewModel.getFiltersOfType(filterType).observe(viewLifecycleOwner, { filters ->
                    chipGroup.removeAllViews()
                    filters.forEach {
                        if (!it.filterObject.active) {
                            val filterChip = FilterChip(it, fragmentView.context)
                            val filterObject = it.filterObject
                            filterChip.setOnClickListener { viewModel.flipFilterActive(filterObject) }
                            filterChip.setOnCloseIconClickListener { viewModel.setFilterInactive(filterObject) }
                            chipGroup.addView(filterChip)
                        }
                    }
                })
            }

            override fun onFilterTypeSwitchClicked(filterType: FilterType) {
                viewModel.flipFilterType(filterType)
            }
        })
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = mLayoutManager

        viewModel.checkBoxVisibilty.observe(viewLifecycleOwner, { visibility ->
            val list = ArrayList<Int>()
            if (!visibility[0]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_ONE)
            }
            if (!visibility[1]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_TWO)
            }
            if (!visibility[2]) {
                list.add(FilterType.FILTERCOLUMN_CHECKBOX_THREE)
            }
            adapter.updateExcludedTypes(list)
        })
        viewModel.filterTypes.observe(viewLifecycleOwner, { filterTypes -> adapter.updateList(filterTypes) })

        return fragmentView
    }

    private fun makeCurrentFilterChip(fullFilter: FullFilter): Chip {
        val filterChip: Chip = FilterChip(fullFilter, requireView().context)
        filterChip.setOnCloseIconClickListener { viewModel.deactivateFilter(fullFilter.filterObject) }
        return filterChip
    }
}
