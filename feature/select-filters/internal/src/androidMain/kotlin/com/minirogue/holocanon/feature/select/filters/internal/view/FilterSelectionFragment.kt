package com.minirogue.holocanon.feature.select.filters.internal.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.holocanon.feature.select.filters.internal.R
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import filters.model.FilterGroup
import filters.model.MediaFilter

@AndroidEntryPoint
internal class FilterSelectionFragment : Fragment() {

    private val viewModel: FilterSelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            HolocanonTheme {
                val state = viewModel.state.collectAsState()
                Scaffold(Modifier.fillMaxSize()) { padding ->
                    FilterSelection(Modifier.padding(padding), state)
                }
            }
        }
    }

    @Composable
    private fun FilterSelection(modifier: Modifier = Modifier, state: State<FilterSelectionState>) {
        Column(modifier = modifier.fillMaxSize()) {
            ActiveFilters(state)
        }
    }

    @Composable
    private fun ColumnScope.ActiveFilters(state: State<FilterSelectionState>) {
        val activeFilters = remember { derivedStateOf { state.value.activeFilters } }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .horizontalScroll(rememberScrollState()),
        ) {
            activeFilters.value.forEach { filter ->
                ActiveFilterChip(filter) { viewModel.deactivateFilter(filter) }
            }
        }
        HorizontalDivider()
        val filterGroups = remember { derivedStateOf { state.value.filterGroups } }
        filterGroups.value.forEach { filterGroupMapEntry ->
            FilterTypeSubMenu(
                filterGroup = filterGroupMapEntry.key,
                filters = filterGroupMapEntry.value,
                onGroupCheckChanged = { viewModel.flipFilterType(filterGroupMapEntry.key) },
                onFilterClicked = { filter -> viewModel.flipFilterActive(filter) },
            )
        }
    }

    @Composable
    private fun ActiveFilterChip(filter: MediaFilter, onDismiss: () -> Unit) {
        InputChip(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            onClick = { onDismiss() },
            label = { Text(filter.name) },
            selected = filter.isPositive,
            leadingIcon = {
                Icon(
                    if (filter.isPositive) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (filter.isPositive) {
                        stringResource(R.string.select_filters_include)
                    } else {
                        stringResource(R.string.select_filters_exclude)
                    },
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

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ColumnScope.FilterTypeSubMenu(
        filterGroup: FilterGroup,
        filters: List<MediaFilter>,
        onGroupCheckChanged: (Boolean) -> Unit,
        onFilterClicked: (MediaFilter) -> Unit,
    ) {
        val isExpanded = remember { mutableStateOf(false) }
        val dropDownArrowRotation = animateFloatAsState(
            targetValue = if (isExpanded.value) 0f else -90f,
            animationSpec = tween(durationMillis = 350),
        )
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { isExpanded.value = !isExpanded.value },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row {
                Text(filterGroup.text)
                Icon(
                    modifier = Modifier.rotate(dropDownArrowRotation.value),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
            Switch(
                checked = filterGroup.isFilterPositive,
                onCheckedChange = { onGroupCheckChanged(it) },
            )
        }
        AnimatedVisibility(isExpanded.value) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        modifier = Modifier.padding(4.dp),
                        onClick = { onFilterClicked(filter) },
                        label = { Text(filter.name) },
                        selected = filter.isActive,
                        leadingIcon = {
                            if (filter.isActive) {
                                Icon(
                                    if (filter.isPositive) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = if (filter.isPositive) {
                                        stringResource(R.string.select_filters_include)
                                    } else {
                                        stringResource(R.string.select_filters_exclude)
                                    },
                                )
                            } else {
                                null
                            }
                        },
                    )
                }
            }
        }
    }
}
