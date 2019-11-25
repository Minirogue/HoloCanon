package com.minirogue.starwarscanontracker.model.room.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType

data class FullFilter(@Embedded val filterObject: FilterObject,
                       val is_positive: Boolean)