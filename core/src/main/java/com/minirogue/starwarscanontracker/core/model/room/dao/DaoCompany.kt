package com.minirogue.starwarscanontracker.core.model.room.dao

import androidx.room.*
import com.minirogue.starwarscanontracker.core.model.room.entity.CompanyDto

@Dao
interface DaoCompany {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(companyDto: CompanyDto): Long

    @Query("SELECT * FROM companies")
    fun getAllNonLive(): List<CompanyDto>

    @Query("SELECT * FROM companies")

    suspend fun getAllCompanies(): List<CompanyDto>
}
