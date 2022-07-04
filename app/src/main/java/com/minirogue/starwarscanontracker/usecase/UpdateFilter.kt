package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateFilter @Inject constructor(private val daoFilter: DaoFilter) {
    /**
     * Persist a FilterObject to the database.
     */
    operator fun invoke(filterObject: FilterObject) = GlobalScope.launch(Dispatchers.Default) {
        daoFilter.update(filterObject)
    }

    /**
     * Persist a FilterType to the database.
     */
    operator fun invoke(filterType: FilterType) = GlobalScope.launch(Dispatchers.Default) {
        daoFilter.update(filterType)
    }
}
