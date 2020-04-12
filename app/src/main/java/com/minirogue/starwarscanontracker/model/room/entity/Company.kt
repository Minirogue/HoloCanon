package com.minirogue.starwarscanontracker.model.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companies")
data class Company(@PrimaryKey val id: Int, val companyName: String)