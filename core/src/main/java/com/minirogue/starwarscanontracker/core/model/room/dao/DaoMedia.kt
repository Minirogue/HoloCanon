package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoMedia {
    //This class defines the insert, query, update, and delete methods for the room
    //The following are used for MediaItems
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mediaItem: MediaItem): Long

    @Update
    suspend fun update(mediaItem: MediaItem)

    @Query("SELECT * FROM media_notes WHERE media_id = :mediaId")
    fun getMediaNotesById(mediaId: Int): LiveData<MediaNotes>

    @Query("SELECT * FROM media_items WHERE id = :mediaID LIMIT 1")
    fun getMediaItemById(mediaID: Int): LiveData<MediaItem>

    //The following are for MediaNotes interactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(um: MediaNotes): Long

    @Update
    fun update(um: MediaNotes)

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    fun getMediaNotesBySeries(series: Int): LiveData<List<MediaNotes>>

    @Query(
        "SELECT media_notes.* FROM media_items INNER JOIN media_notes " +
                "ON media_items.id = media_notes.media_id WHERE media_items.series = :series"
    )
    fun getMediaNotesBySeriesNonLive(series: Int): List<MediaNotes>

    //The following return MediaAndNotes objects
    @RawQuery(observedEntities = [MediaItem::class, MediaNotes::class])
    fun getMediaAndNotesRawQuery(query: SupportSQLiteQuery): LiveData<List<MediaAndNotes>>

    @Query(
        "SELECT media_items.*,media_notes.* FROM media_items " +
                "INNER JOIN media_notes ON media_items.id = media_notes.media_id " +
                "WHERE series = :seriesId"
    )
    fun getMediaAndNotesForSeries(seriesId: Int): Flow<List<MediaAndNotes>>
}
