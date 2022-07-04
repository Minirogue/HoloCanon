package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilter @Inject constructor(private val daoFilter: DaoFilter) {
    suspend operator fun invoke(id: Int, typeId: Int): FilterObject? = withContext(Dispatchers.Default) {
        daoFilter.getFilter(id,
            typeId)
    }
}
