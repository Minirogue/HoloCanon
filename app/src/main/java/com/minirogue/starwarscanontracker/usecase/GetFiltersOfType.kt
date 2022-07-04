package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFiltersOfType @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
) {
    operator fun invoke(typeId: Int): Flow<List<FilterObject>> = daoFilter.getFiltersWithType(typeId).map {
        if (typeId == FilterType.FILTERCOLUMN_TYPE) {
            it.filter { filterObject -> filterObject !in getPermanentFilters() }
        } else it
    }
}
