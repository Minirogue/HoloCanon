package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFilterTypes @Inject constructor(private val daoFilter: DaoFilter) {

    operator fun invoke(): Flow<List<FilterType>> = daoFilter.getAllFilterTypes()
}
