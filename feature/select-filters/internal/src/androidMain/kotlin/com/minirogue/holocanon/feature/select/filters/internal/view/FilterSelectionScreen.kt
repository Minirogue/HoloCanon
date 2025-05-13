package com.minirogue.holocanon.feature.select.filters.internal.view

import ActiveFilterChip
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.holocanon.feature.select.filters.internal.R
import filters.model.FilterGroup
import filters.model.MediaFilter

@Composable
internal fun FilterSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterSelectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        ActiveFilters(
            state = state,
            deactivateFilter = viewModel::deactivateFilter,
            flipFilterType = viewModel::flipFilterType,
            flipFilterActive = viewModel::flipFilterActive,
        )
    }
}

@Composable
private fun ColumnScope.ActiveFilters(
    state: FilterSelectionState,
    deactivateFilter: (MediaFilter) -> Unit,
    flipFilterType: (FilterGroup) -> Unit,
    flipFilterActive: (MediaFilter) -> Unit,
) {
    val activeFilters = remember { derivedStateOf { state.activeFilters } }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState()),
    ) {
        activeFilters.value.forEach { filter ->
            ActiveFilterChip(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                filter,
            ) { deactivateFilter(filter) }
        }
    }
    HorizontalDivider()
    state.filterGroups.forEach { filterGroupMapEntry ->
        FilterTypeSubMenu(
            filterGroup = filterGroupMapEntry.key,
            filters = filterGroupMapEntry.value,
            onGroupCheckChanged = { flipFilterType(filterGroupMapEntry.key) },
            onFilterClicked = { filter -> flipFilterActive(filter) },
        )
    }
}

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
                                    stringResource(R.string.filter_ui_include)
                                } else {
                                    stringResource(R.string.filter_ui_exclude)
                                },
                            )
                        }
                    },
                )
            }
        }
    }
}
