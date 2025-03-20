package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import kotlinx.coroutines.flow.Flow

interface GetMediaListWithNotes {
    operator fun invoke(): Flow<List<MediaAndNotes>>
}
