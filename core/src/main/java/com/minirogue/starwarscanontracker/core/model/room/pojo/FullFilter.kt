package com.minirogue.starwarscanontracker.core.model.room.pojo

import androidx.room.Embedded
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject

@Suppress("ConstructorParameterNaming") // requires a schema change
data class FullFilter(@Embedded val filterObject: FilterObject, val is_positive: Boolean)
