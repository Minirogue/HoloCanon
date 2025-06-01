package internal.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zacsweers.metro.Provider

@Composable
internal fun FilterSelectionScreen(
    viewModelProvider: Provider<FilterSelectionViewModel>,
    modifier: Modifier = Modifier,
    viewModel: FilterSelectionViewModel = viewModel { viewModelProvider() },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ActiveFilters(
            state = state,
            deactivateFilter = viewModel::deactivateFilter,
        )
        HorizontalDivider()
        state.filterGroups.forEach { filterGroupMapEntry ->
            FilterTypeSubMenu(
                modifier = Modifier.padding(4.dp),
                filterGroup = filterGroupMapEntry.key,
                filters = filterGroupMapEntry.value,
                onGroupCheckChanged = viewModel::flipFilterType,
                onFilterClicked = viewModel::flipFilterActive,
            )
        }
    }
}
