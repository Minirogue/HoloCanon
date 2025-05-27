package com.holocanon.library.sorting.usecase

import com.holocanon.library.sorting.model.SortStyle

fun interface SaveSortStyle {
    suspend fun saveSortStyle(style: SortStyle)
}
