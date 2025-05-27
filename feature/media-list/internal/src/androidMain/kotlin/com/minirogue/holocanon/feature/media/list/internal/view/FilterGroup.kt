package com.minirogue.holocanon.feature.media.list.internal.view

import ActiveFilterChip
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.holocanon.feature.media.list.internal.R
import com.holocanon.library.sorting.model.SortStyle
import filters.model.MediaFilter

@Composable
internal fun FilterGroup(
    modifier: Modifier = Modifier,
    activeFilters: List<MediaFilter>,
    sortStyle: SortStyle,
    onTapSortStyle: () -> Unit,
    onDismissFilter: (MediaFilter) -> Unit,
) = Row(
    modifier = modifier.horizontalScroll(rememberScrollState()),
) {
    val sortArrowRotation = animateFloatAsState(
        targetValue = if (sortStyle.ascending) 0f else -180f,
        animationSpec = tween(durationMillis = 1000),
    )
    InputChip(
        modifier = Modifier.padding(horizontal = 2.dp),
        onClick = { onTapSortStyle() },
        selected = true,
        label = { Text(sortStyle.getText()) },
        leadingIcon = {
            Icon(
                modifier = Modifier.rotate(sortArrowRotation.value),
                painter = painterResource(R.drawable.media_list_ascending_sort),
                contentDescription = if (sortStyle.ascending) {
                    stringResource(R.string.media_list_ascending)
                } else {
                    stringResource(R.string.media_list_descending)
                },
            )
        },
    )
    activeFilters.forEach {
        ActiveFilterChip(
            modifier = Modifier.padding(horizontal = 2.dp),
            filter = it,
        ) {
            onDismissFilter(it)
        }
    }
}
