package com.minirogue.starwarscanontracker.core.model.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class CompanyDto(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "company_name") val companyName: String
)
