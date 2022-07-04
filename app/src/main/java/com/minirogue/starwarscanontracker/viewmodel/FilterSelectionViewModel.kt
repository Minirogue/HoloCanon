package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.core.model.PrefsRepo
import com.minirogue.starwarscanontracker.core.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.usecase.GetActiveFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FilterSelectionViewModel @Inject constructor(
    private val repository: SWMRepository,
    private val getActiveFilters: GetActiveFilters,
    prefsRepo: PrefsRepo,
) : ViewModel() {

    val filterTypes = repository.getAllFilterTypes()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility

    fun flipFilterType(filterType: FilterType) {
        filterType.isFilterPositive = !filterType.isFilterPositive
        repository.update(filterType)
    }

    fun flipFilterActive(filterObject: FilterObject) {
        filterObject.active = !filterObject.active
        repository.update(filterObject)
    }

    fun setFilterInactive(filterObject: FilterObject) {
        filterObject.active = false
        repository.update(filterObject)
    }

    fun getFiltersOfType(filterType: FilterType): Flow<List<FullFilter>> =
        repository.getFiltersOfType(filterType.typeId).map { filterList ->
            filterList.map {
                FullFilter(it, filterType.isFilterPositive)
            }
        }

    fun getActiveFilters() = getActiveFilters.invoke()

    fun deactivateFilter(filterObject: FilterObject) {
        filterObject.active = false
        repository.update(filterObject)
    }
}
