package com.minirogue.holocanon.feature.select.filters.internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.GetActiveFilters
import filters.GetAllFilterTypes
import filters.GetFiltersOfType
import filters.UpdateFilter
import filters.model.FilterGroup
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

internal data class FilterSelectionState(val allFilterTypes: List<FilterGroup>)

@HiltViewModel
internal class FilterSelectionViewModel @Inject constructor(
    private val getActiveFilters: GetActiveFilters,
    private val getFiltersOfType: GetFiltersOfType,
    private val updateFilter: UpdateFilter,
    getAllFilterTypes: GetAllFilterTypes,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    val state: StateFlow<FilterSelectionState>
        private field = MutableStateFlow(FilterSelectionState())

    init {
        getAllFilterTypes()
            .onEach { }
    }

    val checkboxSettings = getCheckboxSettings()

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

    fun getFiltersOfType(filterType: FilterType): Flow<List<MediaFilter>> =
        getFiltersOfType.invoke(filterType)

    fun getActiveFilters() = getActiveFilters.invoke()

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
