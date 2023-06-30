package filters

import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter

fun FullFilter.toMediaFilter(): MediaFilter = MediaFilter(
    id = filterObject.id,
    name = filterObject.displayText,
    filterType = filterTypeFromInt(filterObject.filterType),
    isPositive = is_positive,
    isActive = filterObject.active
)

fun filterTypeFromInt(intType: Int) =
    FilterType.values().first { it.legacyIntegerConversion == intType }

fun MediaFilter.toFilterObject(): FilterObject = FilterObject(id, filterType.legacyIntegerConversion, isActive, name)