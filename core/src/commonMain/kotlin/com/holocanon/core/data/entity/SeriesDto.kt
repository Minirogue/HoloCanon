package com.holocanon.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "series")
class SeriesDto {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Int = 0

    @ColumnInfo(name = "title")
    var title: String = "series not found"

    @ColumnInfo(name = "description")
    var description: String = ""

    @ColumnInfo(name = "image")
    var imageURL: String = ""
}
