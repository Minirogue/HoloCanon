package com.minirogue.holocanon.feature.select.filters.internal.view

import ActiveFilterChip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import filters.model.MediaFilter

@Composable
internal fun ActiveFilters(
    modifier: Modifier = Modifier,
    state: FilterSelectionState,
    deactivateFilter: (MediaFilter) -> Unit,
) = Row(
    modifier = modifier.horizontalScroll(rememberScrollState()),
) {
    state.activeFilters.forEach { filter ->
        ActiveFilterChip(
            modifier = Modifier.padding(horizontal = 2.dp),
            filter,
        ) { deactivateFilter(filter) }
    }
}
