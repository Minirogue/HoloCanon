package internal.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import filters.model.FilterGroup
import filters.model.MediaFilter
import holocanon.feature.select_filters.internal.generated.resources.Res
import holocanon.feature.select_filters.internal.generated.resources.select_filters_exclude
import holocanon.feature.select_filters.internal.generated.resources.select_filters_include
import holocanon.feature.select_filters.internal.generated.resources.select_filters_user_defined
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CheckboxFilterSubMenu(
    modifier: Modifier = Modifier,
    checkboxFilters: Map<FilterGroup, List<MediaFilter>>,
    onGroupCheckChanged: (FilterGroup) -> Unit,
    onFilterClicked: (MediaFilter) -> Unit,
) = Card(modifier = modifier.fillMaxWidth()) {
    Column {
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(Res.string.select_filters_user_defined))
                Icon(
                    modifier = Modifier.rotate(dropDownArrowRotation.value),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            }
        }
        AnimatedVisibility(isExpanded.value) {
            Column {
            checkboxFilters.forEach { (filterGroup, filters) ->
                HorizontalDivider()
                CheckboxFilterRow(
                        modifier = Modifier.fillMaxWidth(),
                        filterGroup = filterGroup,
                        filter = filters.first(),
                        onGroupCheckChanged = onGroupCheckChanged,
                        onFilterClicked = onFilterClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun CheckboxFilterRow(
    modifier: Modifier = Modifier,
    filterGroup: FilterGroup,
    filter: MediaFilter,
    onGroupCheckChanged: (FilterGroup) -> Unit,
    onFilterClicked: (MediaFilter) -> Unit,
) = Row(
    modifier = modifier.wrapContentHeight(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    FilterChip(
        modifier = Modifier.padding(horizontal = 2.dp),
        onClick = { onFilterClicked(filter) },
        label = { Text(filter.name) },
        selected = filter.isActive,
        leadingIcon = {
            if (filter.isActive) {
                Icon(
                    if (filter.isPositive) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (filter.isPositive) {
                        stringResource(Res.string.select_filters_include)
                    } else {
                        stringResource(Res.string.select_filters_exclude)
                    },
                )
            }
        },
    )
    GroupSwitch(filterGroup, onGroupCheckChanged)
}