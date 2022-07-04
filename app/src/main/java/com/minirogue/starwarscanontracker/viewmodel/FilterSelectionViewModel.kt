package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.core.model.PrefsRepo
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.usecase.GetActiveFilters
import com.minirogue.starwarscanontracker.usecase.GetAllFilterTypes
import com.minirogue.starwarscanontracker.usecase.GetFiltersOfType
import com.minirogue.starwarscanontracker.usecase.UpdateFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    fun flipFilterType(filterType: FilterType) {
        filterType.isFilterPositive = !filterType.isFilterPositive
        updateFilter(filterType)
    }

    fun flipFilterActive(filterObject: FilterObject) {
        filterObject.active = !filterObject.active
        updateFilter(filterObject)
    }

    fun setFilterInactive(filterObject: FilterObject) {
        filterObject.active = false
        updateFilter(filterObject)
    }

    fun getFiltersOfType(filterType: FilterType): Flow<List<FullFilter>> =
        getFiltersOfType(filterType.typeId).map { filterList ->
            filterList.map {
                FullFilter(it, filterType.isFilterPositive)
            }
        }

    fun getActiveFilters() = getActiveFilters.invoke()

    fun deactivateFilter(filterObject: FilterObject) {
        filterObject.active = false
        updateFilter(filterObject)
    }
}
