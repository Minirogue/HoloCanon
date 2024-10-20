package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.minirogue.media.notes.CheckBoxNumber
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotesDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
abstract class DaoMedia {
    //This class defines the insert, query, update, and delete methods for the room
    //The following are used for MediaItems
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(mediaItemDto: MediaItemDto): Long

    @Update
    abstract suspend fun update(mediaItemDto: MediaItemDto)

    @Query("DELETE FROM media_notes")
    abstract suspend fun clearAllMediaNotes()

    @Query("SELECT * FROM media_notes")
    abstract fun getAllMediaNotes(): Flow<List<MediaNotesDto>>

    @Query("SELECT * FROM media_notes WHERE media_id = :mediaId")
    abstract fun getMediaNotesById(mediaId: Long): Flow<MediaNotesDto>

    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    abstract fun getMediaItemById(mediaID: Int): LiveData<MediaItemDto>

    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(um: MediaNotesDto): Long

    @Update
    abstract suspend fun update(um: MediaNotesDto)

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    abstract fun getMediaNotesBySeries(series: Int): LiveData<List<MediaNotesDto>>

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    abstract fun getMediaNotesBySeriesNonLive(series: Int): List<MediaNotesDto>

    //The following return MediaAndNotes objects
    @RawQuery(observedEntities = [MediaItemDto::class, MediaNotesDto::class])
    abstract fun getMediaAndNotesRawQuery(query: SupportSQLiteQuery): Flow<List<MediaAndNotesDto>>

    @Query(
        "SELECT media_items.*,media_notes.* FROM media_items " +
                "INNER JOIN media_notes ON media_items.id = media_notes.media_id " +
                "WHERE series = :seriesId"
    )
    abstract fun getMediaAndNotesForSeries(seriesId: Int): Flow<List<MediaAndNotesDto>>

    @Transaction
    open suspend fun updateMediaNote(
        checkBox: CheckBoxNumber,
        mediaItemId: Long,
        newValue: Boolean
    ) {
        val oldNotes = getMediaNotesById(mediaItemId).first()
        val newNotes = when (checkBox) {
            CheckBoxNumber.CheckBox1 -> oldNotes.apply { isBox1Checked = newValue }
            CheckBoxNumber.CheckBox2 -> oldNotes.apply { isBox2Checked = newValue }
            CheckBoxNumber.CheckBox3 -> oldNotes.apply { isBox3Checked = newValue }
        }
        update(newNotes)
    }
}
