package filters

import android.util.Log
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObjectDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.FullFilter
import filters.model.FilterType
import filters.model.MediaFilter

fun FullFilter.toMediaFilter(): MediaFilter? = try {
    MediaFilter(
        id = filterObjectDto.id,
        name = filterObjectDto.displayText ?: "NULL", // Legacy Java nullability
        filterType = filterTypeFromInt(filterObjectDto.filterType)
            ?: error("filter type with legacy value ${filterObjectDto.filterType} not found"),
        isPositive = is_positive,
        isActive = filterObjectDto.active
    )
} catch (e: IllegalStateException) {
    Log.w("toMediaFilter()", "error converting media filter from DTO", e)
    null
}

fun filterTypeFromInt(intType: Int) =
    FilterType.entries.find { it.legacyIntegerConversion == intType }

fun MediaFilter.toFilterObject(): FilterObjectDto =
    FilterObjectDto(id, filterType.legacyIntegerConversion, isActive, name)
