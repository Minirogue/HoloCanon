package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.adapter.FilterSelectionAdapter
import com.minirogue.starwarscanontracker.viewmodel.FilterSelectionViewModel
import kotlinx.android.synthetic.main.fragment_filter_selection.view.*
import javax.inject.Inject

class FilterSelectionFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: FilterSelectionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_filter_selection, container, false)
        (activity!!.application as CanonTrackerApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FilterSelectionViewModel::class.java)
        val recyclerView = fragmentView.general_recyclerview

        val activeChipGroup = fragmentView.selected_chipgroup

        viewModel.getActiveFilters().observe(viewLifecycleOwner, Observer { activeFilters -> activeChipGroup.removeAllViews(); activeFilters.forEach { activeChipGroup.addView(makeCurrentFilterChip(it)) } })

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(fragmentView.context)
        val adapter = FilterSelectionAdapter()
        adapter.setOnClickListeners(object : FilterSelectionAdapter.OnClickListeners {
            override fun onFilterClicked(filterObject: FilterObject) {
                viewModel.flipFilterActive(filterObject)
            }

            override fun setFilterGroupObservation(chipGroup: ChipGroup, filterType: FilterType) {
                viewModel.getFiltersOfType(filterType).observe(viewLifecycleOwner, Observer { filters ->
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
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = mLayoutManager


        viewModel.filterTypes.observe(viewLifecycleOwner, Observer { filterTypes -> adapter.updateList(filterTypes) })

        return fragmentView
    }

    private fun makeCurrentFilterChip(fullFilter: FullFilter): Chip {
        val filterChip: Chip = FilterChip(fullFilter, view!!.context)
        filterChip.setOnCloseIconClickListener { viewModel.deactivateFilter(fullFilter.filterObject) }
        return filterChip
    }

}