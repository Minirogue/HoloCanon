package com.minirogue.starwarscanontracker.model.room.pojo

import androidx.room.Embedded
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject

@Suppress("ConstructorParameterNaming") // requires a schema change
data class FullFilter(@Embedded val filterObject: FilterObject, val is_positive: Boolean)
