package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterTypeDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.GetCheckboxText
import javax.inject.Inject

class GetCheckboxTextImpl @Inject constructor(private val daoFilter: DaoFilter) : GetCheckboxText {
    override operator fun invoke(): Flow<Array<String>> = daoFilter.getCheckBoxFilterTypes().map { filterTypeList ->
        arrayOf("", "", "").apply {
            filterTypeList.forEach {
                when (it.typeId) {
                    FilterTypeDto.FILTERCOLUMN_CHECKBOX_ONE -> this[0] = it.text
                    FilterTypeDto.FILTERCOLUMN_CHECKBOX_TWO -> this[1] = it.text
                    FilterTypeDto.FILTERCOLUMN_CHECKBOX_THREE -> this[2] = it.text
                }
            }
        }
    }
}
