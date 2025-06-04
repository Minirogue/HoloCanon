package com.holocanon.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minirogue.media.notes.model.MediaNotes

@Entity(
    tableName = "media_notes",
    foreignKeys = [
        ForeignKey(
            entity = MediaItemDto::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MediaNotesDto(
    @field:PrimaryKey
    @ColumnInfo(name = "media_id")
    val mediaId: Int,
    @ColumnInfo(name = "checkbox_1")
    var isBox1Checked: Boolean = false,
    @ColumnInfo(name = "checkbox_2")
    var isBox2Checked: Boolean = false,
    @ColumnInfo(name = "checkbox_3")
    var isBox3Checked: Boolean = false,
) {
    fun toMediaNotes() = MediaNotes(isBox1Checked, isBox2Checked, isBox3Checked)
}
