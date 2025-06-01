package com.holocanon.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.holocanon.core.data.entity.SeriesDto
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoSeries {
    @Query("SELECT * FROM series")
    suspend fun getAllNonLive(): List<SeriesDto>

    @Query("SELECT * FROM series")
    fun getAllSeries(): Flow<List<SeriesDto>>

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    suspend fun getSeries(id: Int): SeriesDto?

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeriesFlow(id: Int): Flow<SeriesDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seriesDto: SeriesDto): Long
}
