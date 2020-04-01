package com.minirogue.starwarscanontracker.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "media_notes", foreignKeys = [ForeignKey(entity = MediaItem::class, parentColumns = ["id"], childColumns = ["mediaId"], onDelete = ForeignKey.CASCADE)])
class MediaNotes(@field:PrimaryKey val mediaId: Int,
                 @ColumnInfo(name = "watched_or_read")
                 var isUserChecked1: Boolean = false,
                 @ColumnInfo(name = "want_to_watch_or_read")
                 var isUserChecked2: Boolean = false,
                 @ColumnInfo(name = "owned")
                 var isUserChecked3: Boolean = false) {

    fun flipCheck1() {
        isUserChecked1 = !isUserChecked1
    }

    fun flipCheck2() {
        isUserChecked2 = !isUserChecked2
    }

    fun flipCheck3() {
        isUserChecked3 = !isUserChecked3
    }

}