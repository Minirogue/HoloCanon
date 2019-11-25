package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.minirogue.starwarscanontracker.model.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import org.koin.core.KoinComponent
import org.koin.core.inject

internal class FilterSelectionViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val repository: SWMRepository by inject()
    val filterTypes = repository.getAllFilterTypes()


    fun flipFilterType(filterType: FilterType) {
        filterType.isFilterPositive = !filterType.isFilterPositive
        repository.update(filterType)
    }

    fun flipFilterActive(filterObject: FilterObject) {
        filterObject.active = !filterObject.active
        repository.update(filterObject)
    }
    fun setFilterInactive(filterObject: FilterObject){
        filterObject.active = false
        repository.update(filterObject)
    }

    fun getFiltersOfType(filterType: FilterType) = Transformations.map(repository.getFiltersOfType(filterType.typeId)) { filterList -> filterList.map { FullFilter(it, filterType.isFilterPositive) } }

    fun getActiveFilters() = repository.getActiveFilters()

    fun deactivateFilter(filterObject: FilterObject){
        filterObject.active = false
        repository.update(filterObject)
    }

}
