package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_ONE
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_THREE
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_CHECKBOX_TWO
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_PUBLISHER
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_SERIES
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto.Companion.FILTERCOLUMN_TYPE
import filters.FilterGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllFilterTypes @Inject constructor(private val daoFilter: DaoFilter) {

    operator fun invoke(): Flow<List<FilterGroup>> = daoFilter.getAllFilterTypes().map { list -> list.map { it.toFilterGroup() } }
    private fun FilterTypeDto.toFilterGroup(): FilterGroup =
        FilterGroup(type = getTypeFromInt(typeId), isFilterPositive = isFilterPositive, text = text)

    private fun getTypeFromInt(typeId: Int): filters.FilterType = when (typeId) {
        FILTERCOLUMN_TYPE -> filters.FilterType.MediaType
        FILTERCOLUMN_CHECKBOX_ONE -> filters.FilterType.CheckboxOne
        FILTERCOLUMN_CHECKBOX_TWO -> filters.FilterType.CheckboxTwo
        FILTERCOLUMN_CHECKBOX_THREE -> filters.FilterType.CheckboxThree
        FILTERCOLUMN_SERIES -> filters.FilterType.Series
        FILTERCOLUMN_PUBLISHER -> filters.FilterType.Publisher
        else -> throw IllegalArgumentException("$typeId is not a valid filter type id")
    }
}
