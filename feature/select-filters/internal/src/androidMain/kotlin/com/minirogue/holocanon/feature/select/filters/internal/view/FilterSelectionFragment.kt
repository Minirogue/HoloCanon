package com.minirogue.holocanon.feature.select.filters.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.minirogue.holocanon.feature.select.filters.internal.R
import com.minirogue.starwarscanontracker.view.FilterChip
import com.minirogue.starwarscanontracker.view.collectWithLifecycle
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter

@AndroidEntryPoint
internal class FilterSelectionFragment : Fragment() {

    private val viewModel: FilterSelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            HolocanonTheme {
                val state = viewModel.state.collectAsState()
                FilterSelection(state)
            }
        }
    }

    @Composable
    private fun FilterSelection(state: State<FilterSelectionState>) {
        Column(modifier = Modifier.fillMaxSize()) {
            ActiveFilters(state)
        }
    }

    @Composable
    private fun ActiveFilters(state: State<FilterSelectionState>) {
        val activeFilters = remember { derivedStateOf { state.value.activeFilters } }
        LazyRow(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            items(activeFilters.value) { filter ->
                ActiveFilterChip(filter) { /* TODO */ }
            }
        }
    }

    @Composable
    private fun ActiveFilterChip(filter: MediaFilter, onDismiss: () -> Unit) {
        println("test-log: ActiveFilterChip($filter)")
        InputChip(
            onClick = { onDismiss() },
            label = { Text(filter.name) },
            selected = filter.isPositive,
            leadingIcon = {
                Icon(
                    if (filter.isPositive) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (filter.isPositive) {
                        stringResource(R.string.select_filters_include)
                    } else stringResource(R.string.select_filters_exclude)
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.select_filters_dismiss_filter),
                )
            },
        )
    }
//        val fragmentBinding = SelectFiltersFragmentBinding.inflate(inflater, container, false)
//        val fragmentContext = fragmentBinding.root.context
//
//        val recyclerView = fragmentBinding.generalRecyclerview
//
//        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(fragmentContext)
//        val adapter = FilterSelectionAdapter()
//        adapter.setOnClickListeners(object : FilterSelectionAdapter.OnClickListeners {
//            override fun setFilterGroupObservation(chipGroup: ChipGroup, filterGroup: FilterGroup) {
//                viewModel.getFiltersOfType(filterGroup.type)
//                    .collectWithLifecycle { filters ->
//                        chipGroup.removeAllViews()
//                        filters.forEach { mediaFilter ->
//                            if (!mediaFilter.isActive) {
//                                val filterChip = FilterChip(mediaFilter, fragmentContext)
//                                filterChip.setOnClickListener {
//                                    viewModel.flipFilterActive(mediaFilter)
//                                }
//                                filterChip.setOnCloseIconClickListener {
//                                    viewModel.setFilterInactive(mediaFilter)
//                                }
//                                chipGroup.addView(filterChip)
//                            }
//                        }
//                    }
//            }
//
//            override fun onFilterTypeSwitchClicked(filterGroup: FilterGroup) {
//                viewModel.flipFilterType(filterGroup)
//            }
//        })
//        recyclerView.setHasFixedSize(false)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = mLayoutManager
//
//        viewModel.state.collectWithLifecycle { state ->
//            println("test-log: state updated to $state")
//            with (fragmentBinding.selectedChipgroup) {
//                removeAllViews()
//                state.activeFilters.forEach { addView(makeCurrentFilterChip(it)) }
//            }
//
//            adapter.updateList(state.allFilterTypes)
//
//            val checkboxSettings = state.checkboxSettings
//            if (checkboxSettings != null) {
//                val list = ArrayList<FilterType>()
//                if (!checkboxSettings.checkbox1Setting.isInUse) {
//                    list.add(FilterType.CheckboxOne)
//                }
//                if (!checkboxSettings.checkbox2Setting.isInUse) {
//                    list.add(FilterType.CheckboxTwo)
//                }
//                if (!checkboxSettings.checkbox3Setting.isInUse) {
//                    list.add(FilterType.CheckboxThree)
//                }
//                adapter.updateExcludedTypes(list)
//            }
//        }

    private fun makeCurrentFilterChip(mediaFilter: MediaFilter): Chip {
        val filterChip: Chip = FilterChip(mediaFilter, requireView().context)
        filterChip.setOnCloseIconClickListener { viewModel.deactivateFilter(mediaFilter) }
        return filterChip
    }
}
