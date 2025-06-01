package com.holocanon.library.filters.internal

import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObjectDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import filters.model.FilterType
import filters.model.MediaFilter

internal fun FullFilter.toMediaFilter(): MediaFilter = MediaFilter(
    id = filterObjectDto.id,
    name = filterObjectDto.displayText ?: "NULL", // Legacy Java nullability
    filterType = filterTypeFromInt(filterObjectDto.filterType),
    isPositive = is_positive,
    isActive = filterObjectDto.active,
)

private fun filterTypeFromInt(intType: Int) =
    FilterType.entries.first { it.legacyIntegerConversion == intType }

internal fun MediaFilter.toFilterObject(): FilterObjectDto = FilterObjectDto(
    id,
    filterType.legacyIntegerConversion,
    isActive,
    name,
)
