package internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import filters.GetActiveFilters
import filters.GetAllFilterGroups
import filters.UpdateFilter
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

internal data class FilterSelectionState(
    val checkboxFilters: Map<FilterGroup, List<MediaFilter>>,
    val nonCheckboxFilters: Map<FilterGroup, List<MediaFilter>>,
    val activeFilters: List<MediaFilter>,
)

@Inject
internal class FilterSelectionViewModel(
    getActiveFilters: GetActiveFilters,
    private val updateFilter: UpdateFilter,
    getAllFilterGroups: GetAllFilterGroups,
) : ViewModel() {

    val state: Flow<FilterSelectionState> = combine(
        getAllFilterGroups(),
        getActiveFilters(),
    ) { filterGroups, activeFilters ->
        val checkboxFilters = filterGroups.filter {
            it.key.type in setOf(
                FilterType.CheckboxOne,
                FilterType.CheckboxTwo,
                FilterType.CheckboxThree,
            )
        }
        val nonCheckboxFilterGroups = filterGroups.filter {
            it.key.type !in setOf(
                FilterType.CheckboxOne,
                FilterType.CheckboxTwo,
                FilterType.CheckboxThree,
            )
        }
        FilterSelectionState(
            checkboxFilters = checkboxFilters,
            nonCheckboxFilters = nonCheckboxFilterGroups,
            activeFilters = activeFilters,
        )
    }

    fun flipFilterType(filterGroup: FilterGroup) = viewModelScope.launch {
        updateFilter(filterGroup.copy(isFilterPositive = !filterGroup.isFilterPositive))
    }

    fun flipFilterActive(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = !mediaFilter.isActive)
        updateFilter(newMediaFilter)
    }

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
