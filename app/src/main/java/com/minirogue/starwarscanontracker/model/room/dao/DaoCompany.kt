package com.minirogue.starwarscanontracker.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.minirogue.starwarscanontracker.model.room.entity.Company

@Dao
interface DaoCompany {

    @Insert
    fun insert(company: Company): Long

    @Update
    fun update(company: Company)

    @Query("SELECT * FROM companies")
    fun getAllNonLive(): List<Company>
}