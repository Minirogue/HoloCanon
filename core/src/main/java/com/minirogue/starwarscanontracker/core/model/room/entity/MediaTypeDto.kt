package com.minirogue.starwarscanontracker.core.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_types")
data class MediaTypeDto(
    @PrimaryKey
    @ColumnInfo
    val id: Int,
    @ColumnInfo
    val text: String,
)
