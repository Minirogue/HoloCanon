package com.minirogue.starwarscanontracker.model.room.entity

import androidx.room.*

@Entity(tableName = "media_items", foreignKeys = [ForeignKey(entity = MediaType::class, parentColumns = arrayOf("id"), childColumns = arrayOf("type"))], indices = [Index("type"), Index("series")])
data class MediaItem(@PrimaryKey
                     val id: Int,
                     @ColumnInfo(name = "title")
                     var title: String,
                     @ColumnInfo(name = "series")
                     var series: Int,
                     @ColumnInfo(name = "author")
                     var author: String,
                     @ColumnInfo(name = "type")
                     var type: Int,
                     @ColumnInfo(name = "description")
                     var description: String,
                     @ColumnInfo(name = "review")
                     var review: String,
                     @ColumnInfo(name = "image")
                     var imageURL: String,
                     @ColumnInfo(name = "date")
                     var date: String,
                     @ColumnInfo(name = "timeline")
                     var timeline: Double,
                     @ColumnInfo(name = "amazon_link")
                     var amazonLink: String,
                     @ColumnInfo(name = "amazon_stream")
                     var amazonStream: String,
                     @ColumnInfo(name = "publisher")
                     var publisher: Int) {

    override fun toString(): String {
        return title
    }
}