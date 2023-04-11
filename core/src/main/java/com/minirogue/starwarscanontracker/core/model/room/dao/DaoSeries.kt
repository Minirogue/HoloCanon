package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoSeries {
    @Query("SELECT * FROM series")
    fun getAllNonLive(): List<Series>

    @Query("SELECT * FROM series")

    suspend fun getAllSeries(): List<Series>

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeries(id: Int): Series?

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeriesFlow(id: Int): Flow<Series>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(series: Series): Long
}
