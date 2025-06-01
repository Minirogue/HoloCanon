package filters

import com.minirogue.starwarscanontracker.core.data.entity.FilterObjectDto
import com.minirogue.starwarscanontracker.core.data.pojo.FullFilter
import filters.model.FilterType
import filters.model.MediaFilter

fun FullFilter.toMediaFilter(): MediaFilter = MediaFilter(
    id = filterObjectDto.id,
    name = filterObjectDto.displayText ?: "NULL", // Legacy Java nullability
    filterType = filterTypeFromInt(filterObjectDto.filterType),
    isPositive = is_positive,
    isActive = filterObjectDto.active,
)

fun filterTypeFromInt(intType: Int) =
    FilterType.entries.first { it.legacyIntegerConversion == intType }

fun MediaFilter.toFilterObject(): FilterObjectDto = FilterObjectDto(
    id,
    filterType.legacyIntegerConversion,
    isActive,
    name,
)
