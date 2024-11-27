package com.minirogue.starwarscanontracker.core.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import filters.model.FilterType

@Entity(tableName = "filter_type")
class FilterTypeDto(
    @PrimaryKey @ColumnInfo(name = "id") val typeId: Int,
    @ColumnInfo(name = "is_positive") var isFilterPositive: Boolean,
    @ColumnInfo(name = "text") var text: String,
) {

    companion object {
        val FILTERCOLUMN_TYPE = FilterType.MediaType.legacyIntegerConversion
        val FILTERCOLUMN_SERIES = FilterType.Series.legacyIntegerConversion
        val FILTERCOLUMN_PUBLISHER = FilterType.Publisher.legacyIntegerConversion
        val FILTERCOLUMN_CHECKBOX = FilterType.Checkbox.legacyIntegerConversion
    }
}
