package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.CompanyDto

@Dao
interface DaoCompany {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(companyDto: CompanyDto): Long

    @Update
    fun update(companyDto: CompanyDto)

    @Query("SELECT * FROM companies")
    fun getAllNonLive(): List<CompanyDto>
}
