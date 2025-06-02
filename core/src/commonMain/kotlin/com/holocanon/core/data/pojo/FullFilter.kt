package com.holocanon.core.data.pojo

import androidx.room.Embedded
import com.holocanon.core.data.entity.FilterObjectDto

data class FullFilter(@Embedded val filterObjectDto: FilterObjectDto, val is_positive: Boolean)
