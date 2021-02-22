package com.minirogue.starwarscanontracker.model.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.minirogue.starwarscanontracker.model.room.entity.Series

@Dao
interface DaoSeries {
    @Query("SELECT * FROM series")
    fun getAllLive(): LiveData<List<Series>>

    @Query("SELECT * FROM series")
    fun getAllNonLive(): List<Series>

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeries(id: Int): Series?

    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getLiveSeries(id: Int): LiveData<Series>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(series: Series): Long

    @Update
    fun update(series: Series)
}
