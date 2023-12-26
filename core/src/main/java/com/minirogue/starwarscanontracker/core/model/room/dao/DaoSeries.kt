package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minirogue.starwarscanontracker.core.model.room.entity.SeriesDto
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoSeries {
    @Query("SELECT * FROM series")
    fun getAllNonLive(): List<SeriesDto>

    @Query("SELECT * FROM series")
    suspend fun getAllSeries(): List<SeriesDto>

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeries(id: Int): SeriesDto?

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeriesFlow(id: Int): Flow<SeriesDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seriesDto: SeriesDto): Long
}
