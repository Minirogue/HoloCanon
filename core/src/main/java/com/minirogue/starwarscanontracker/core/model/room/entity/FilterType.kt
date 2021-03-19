package com.minirogue.starwarscanontracker.core.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_type")
class FilterType(
    @PrimaryKey @ColumnInfo(name = "id") val typeId: Int,
    @ColumnInfo(name = "is_positive") var isFilterPositive: Boolean,
    @ColumnInfo(name = "text") var text: String,
) {

    companion object {
        const val FILTERCOLUMN_TYPE = 1
        const val FILTERCOLUMN_CHECKBOX_ONE = 3
        const val FILTERCOLUMN_CHECKBOX_TWO = 4
        const val FILTERCOLUMN_CHECKBOX_THREE = 5
        const val FILTERCOLUMN_SERIES = 6
        const val FILTERCOLUMN_PUBLISHER = 7
    }
}
