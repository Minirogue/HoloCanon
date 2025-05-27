package com.holocanon.library.sorting.usecase

import com.holocanon.library.sorting.model.SortStyle
import kotlinx.coroutines.flow.Flow

fun interface GetSortStyle {
    fun getSortStyle(): Flow<SortStyle>
}
