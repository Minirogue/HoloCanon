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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.zacsweers.metro.Provider
import loading.withStateOrLoadingScreen

@Composable
internal fun FilterSelectionScreen(
    viewModelProvider: Provider<FilterSelectionViewModel>,
    modifier: Modifier = Modifier,
    viewModel: FilterSelectionViewModel = viewModel { viewModelProvider() },
) {
    viewModel.state.withStateOrLoadingScreen { state ->
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            ActiveFilters(
                state = state,
                deactivateFilter = viewModel::deactivateFilter,
            )
            HorizontalDivider()
            CheckboxFilterSubMenu(
                modifier = Modifier.padding(4.dp),
                checkboxFilters = state.checkboxFilters,
                onGroupCheckChanged = viewModel::flipFilterType,
                onFilterClicked = viewModel::flipFilterActive,
            )
            state.nonCheckboxFilters.forEach { filterGroupMapEntry ->
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
}
