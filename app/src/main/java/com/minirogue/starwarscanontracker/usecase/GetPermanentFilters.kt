package com.minirogue.starwarscanontracker.usecase

import android.content.SharedPreferences
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPermanentFilters @Inject constructor(
    private val daoType: DaoType,
    private val daoFilter: DaoFilter,
    private val sharedPreferences: SharedPreferences,
) {
    suspend operator fun invoke(): List<FilterObject> = withContext(
        Dispatchers.IO) {
        val filterList = ArrayList<FilterObject>()
        for (type in daoType.allNonLive) {
            if (!sharedPreferences.getBoolean(type.text, true)) {
                filterList.add(daoFilter.getFilter(type.id, FilterType.FILTERCOLUMN_TYPE)
                    ?: FilterObject(-1, -1, false, ""))
            }
        }
        filterList
    }
}
