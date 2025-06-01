package com.holocanon.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "filter_object",
    primaryKeys = ["filter_id", "type_id"],
    foreignKeys = [
        ForeignKey(
            entity = FilterTypeDto::class,
            parentColumns = ["id"],
            childColumns = ["type_id"],
            onDelete = CASCADE,
        ),
    ],
)
data class FilterObjectDto(
    @ColumnInfo(name = "filter_id")
    val id: Int = 0,
    @ColumnInfo(name = "type_id")
    val filterType: Int = 0,
    @ColumnInfo(name = "is_active")
    val active: Boolean = false,
    @ColumnInfo(name = "filter_text")
    val displayText: String? = null,
)
