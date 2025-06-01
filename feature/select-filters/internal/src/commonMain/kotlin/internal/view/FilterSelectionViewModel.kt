package internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import filters.GetActiveFilters
import filters.GetAllFilterGroups
import filters.UpdateFilter
import filters.model.FilterGroup
import filters.model.MediaFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings

internal data class FilterSelectionState(
    val filterGroups: Map<FilterGroup, List<MediaFilter>> = emptyMap(),
    val checkboxSettings: CheckboxSettings? = null,
    val activeFilters: List<MediaFilter> = emptyList(),
)

@Inject
internal class FilterSelectionViewModel constructor(
    getActiveFilters: GetActiveFilters,
    private val updateFilter: UpdateFilter,
    getAllFilterGroups: GetAllFilterGroups,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterSelectionState())
    val state: StateFlow<FilterSelectionState> = _state

    init {
        getAllFilterGroups()
            .onEach { filterGroups -> _state.update { it.copy(filterGroups = filterGroups) } }
            .launchIn(viewModelScope)
        getCheckboxSettings()
            .onEach { checkboxSettings -> _state.update { it.copy(checkboxSettings = checkboxSettings) } }
            .launchIn(viewModelScope)
        getActiveFilters()
            .onEach { activeFilters -> _state.update { it.copy(activeFilters = activeFilters) } }
            .launchIn(viewModelScope)
    }

    fun flipFilterType(filterGroup: FilterGroup) = viewModelScope.launch {
        updateFilter(filterGroup.copy(isFilterPositive = !filterGroup.isFilterPositive))
    }

    fun flipFilterActive(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = !mediaFilter.isActive)
        updateFilter(newMediaFilter)
    }

    fun setFilterInactive(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
