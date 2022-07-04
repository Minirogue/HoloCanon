package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetActiveFilters @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
) {
    operator fun invoke(): Flow<List<FullFilter>> = daoFilter.getActiveFilters().map {
        it.filter { fullFilter -> fullFilter.filterObject !in getPermanentFilters() }
    }
}
