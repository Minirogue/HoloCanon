package com.minirogue.starwarscanontracker.core.model.room.pojo

import androidx.room.Embedded
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObjectDto

@Suppress("ConstructorParameterNaming") // requires a schema change
data class FullFilter(@Embedded val filterObjectDto: FilterObjectDto, val is_positive: Boolean)
