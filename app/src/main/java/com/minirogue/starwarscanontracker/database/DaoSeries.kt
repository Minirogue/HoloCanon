package com.minirogue.starwarscanontracker.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface DaoSeries {
    @Query("SELECT * FROM series")
    fun getAllLive(): LiveData<List<Series>>
    @Query("SELECT * FROM series")
    fun getAllNonLive(): List<Series>
    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getSeries(id: Int): Series
    @Query("SELECT * FROM series WHERE id=:id LIMIT 1")
    fun getLiveSeries(id: Int): LiveData<Series>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(series: Series): Long
    @Update
    fun update(series: Series)
}
