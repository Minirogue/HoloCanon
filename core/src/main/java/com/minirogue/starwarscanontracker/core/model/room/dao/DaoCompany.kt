package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.Company

@Dao
interface DaoCompany {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(company: Company): Long

    @Update
    fun update(company: Company)

    @Query("SELECT * FROM companies")
    fun getAllNonLive(): List<Company>
}
