package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetMediaListWithNotes {
    suspend operator fun invoke(filterList: List<MediaFilter>): Flow<List<MediaAndNotes>>
}

