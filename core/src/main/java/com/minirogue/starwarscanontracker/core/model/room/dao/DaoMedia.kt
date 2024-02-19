package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotesDto
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoMedia {
    //This class defines the insert, query, update, and delete methods for the room
    //The following are used for MediaItems
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mediaItemDto: MediaItemDto): Long

    @Update
    suspend fun update(mediaItemDto: MediaItemDto)

    @Query("SELECT * FROM media_notes WHERE media_id = :mediaId")
    fun getMediaNotesById(mediaId: Int): LiveData<MediaNotesDto>

    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    fun getMediaItemById(mediaID: Int): LiveData<MediaItemDto>

    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(um: MediaNotesDto): Long

    @Update
    fun update(um: MediaNotesDto)

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    fun getMediaNotesBySeries(series: Int): LiveData<List<MediaNotesDto>>

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    fun getMediaNotesBySeriesNonLive(series: Int): List<MediaNotesDto>

    //The following return MediaAndNotes objects
    @RawQuery(observedEntities = [MediaItemDto::class, MediaNotesDto::class])
    fun getMediaAndNotesRawQuery(query: SupportSQLiteQuery): Flow<List<MediaAndNotesDto>>

    @Query(
            "SELECT media_items.*,media_notes.* FROM media_items " +
                    "INNER JOIN media_notes ON media_items.id = media_notes.media_id " +
                    "WHERE series = :seriesId"
    )
    fun getMediaAndNotesForSeries(seriesId: Int): Flow<List<MediaAndNotesDto>>
}
