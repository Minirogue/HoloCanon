package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaTypeDto

@Dao
interface DaoType {

    @Query("SELECT * FROM media_types")
    suspend fun getAllMediaTypes(): List<MediaTypeDto>

}
