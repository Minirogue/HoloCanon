package com.minirogue.starwarscanontracker.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_type")
class FilterType(@PrimaryKey@ColumnInfo(name = "id") val typeId: Int, @ColumnInfo(name = "is_positive") var isFilterPositive: Boolean, @ColumnInfo(name = "text") var text: String) {

    companion object {
        const val FILTERCOLUMN_TYPE = 1
        const val FILTERCOLUMN_CHARACTER = 2
        const val FILTERCOLUMN_OWNED = 3
        const val FILTERCOLUMN_WANTTOREADWATCH = 4
        const val FILTERCOLUMN_HASREADWATCHED = 5
        const val FILTERCOLUMN_SERIES = 6
    }

}