package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType

@Dao
interface DaoType {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(mediaType: MediaType): Long

    @Update
    fun update(mediaType: MediaType?)

    @Query("SELECT * FROM media_types")
    suspend fun allNonLive(): List<MediaType>

    @Query("SELECT * FROM media_types WHERE id=:typeId")
    fun getLiveMediaType(typeId: Int): LiveData<MediaType?>
}
