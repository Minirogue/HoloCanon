package com.minirogue.starwarscanontracker.model.room.entity

import androidx.room.*
@Entity(tableName = "media_items",
        foreignKeys = [ForeignKey(entity = MediaType::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("type"))],
        indices = [Index("type"), Index("series")])
data class MediaItem(@PrimaryKey
                     var id: Int = 0,
                     @ColumnInfo(name = "title", defaultValue = "")
                     var title: String = "",
                     @ColumnInfo(name = "series")
                     var series: Int = 0,
                     @ColumnInfo(name = "author", defaultValue = "")
                     var author: String = "",
                     @ColumnInfo(name = "type")
                     var type: Int = 0,
                     @ColumnInfo(name = "description", defaultValue = "")
                     var description: String = "",
                     @ColumnInfo(name = "review", defaultValue = "")
                     var review: String = "",
                     @ColumnInfo(name = "image", defaultValue = "")
                     var imageURL: String = "",
                     @ColumnInfo(name = "date", defaultValue = "99/99/9999")
                     var date: String = "99/99/9999",
                     @ColumnInfo(name = "timeline")
                     var timeline: Double = 0.0,
                     @ColumnInfo(name = "amazon_link", defaultValue = "")
                     var amazonLink: String = "",
                     @ColumnInfo(name = "amazon_stream", defaultValue = "")
                     var amazonStream: String = "",
                     @ColumnInfo(name = "publisher")
                     var publisher: Int = 0) {

    override fun toString(): String {
        return title
    }
}