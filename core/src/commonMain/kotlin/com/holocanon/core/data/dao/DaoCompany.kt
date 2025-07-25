package com.holocanon.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.holocanon.core.data.entity.CompanyDto
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoCompany {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(companyDto: CompanyDto): Long

    @Query("SELECT * FROM companies")
    suspend fun getAllNonLive(): List<CompanyDto>

    @Query("SELECT * FROM companies")
    fun getAllCompanies(): Flow<List<CompanyDto>>
}
