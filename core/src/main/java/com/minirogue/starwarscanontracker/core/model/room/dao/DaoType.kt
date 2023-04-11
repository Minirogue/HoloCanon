package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaTypeDto

@Dao
interface DaoType {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(mediaTypeDto: MediaTypeDto): Long

    @Update
    fun update(mediaTypeDto: MediaTypeDto?)

    @Query("SELECT * FROM media_types")
    suspend fun allNonLive(): List<MediaTypeDto>

    @Query("SELECT * FROM media_types WHERE id=:typeId")
    fun getLiveMediaType(typeId: Int): LiveData<MediaTypeDto?>
}
