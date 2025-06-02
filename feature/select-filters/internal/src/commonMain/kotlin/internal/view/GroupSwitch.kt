package internal.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import filters.model.FilterGroup
import holocanon.feature.select_filters.internal.generated.resources.Res
import holocanon.feature.select_filters.internal.generated.resources.select_filters_exclude
import holocanon.feature.select_filters.internal.generated.resources.select_filters_include
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GroupSwitch(
    filterGroup: FilterGroup,
    onGroupCheckChanged: (FilterGroup) -> Unit,
) = Column(
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Switch(
        checked = filterGroup.isFilterPositive,
        onCheckedChange = { onGroupCheckChanged(filterGroup) },
    )
    if (filterGroup.isFilterPositive) {
        Text(stringResource(Res.string.select_filters_include))
    } else {
        Text(stringResource(Res.string.select_filters_exclude))
    }
}