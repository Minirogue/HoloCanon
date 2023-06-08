package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.starwarscanontracker.core.model.PrefsRepo
import com.minirogue.starwarscanontracker.usecase.GetAllFilterTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.FilterGroup
import filters.GetActiveFilters
import filters.GetFiltersOfType
import filters.MediaFilter
import filters.UpdateFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterSelectionViewModel @Inject constructor(
    private val getActiveFilters: GetActiveFilters,
    private val getFiltersOfType: GetFiltersOfType,
    private val updateFilter: UpdateFilter,
    getAllFilterTypes: GetAllFilterTypes,
    prefsRepo: PrefsRepo,
) : ViewModel() {

    val filterTypes = getAllFilterTypes()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility

    fun flipFilterType(filterGroup: FilterGroup) = viewModelScope.launch {
        updateFilter(filterGroup.copy (isFilterPositive = !filterGroup.isFilterPositive))
    }

    fun flipFilterActive(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = !mediaFilter.isActive)
        updateFilter(newMediaFilter)
    }

    fun setFilterInactive(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)

    }

    fun getFiltersOfType(filterType: filters.FilterType): Flow<List<MediaFilter>> = getFiltersOfType(filterType)

    fun getActiveFilters() = getActiveFilters.invoke()

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
       val newMediaFilter =  mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
