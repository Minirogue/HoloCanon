package com.minirogue.starwarscanontracker.core.usecase

import com.holocanon.core.model.MediaAndNotes
import kotlinx.coroutines.flow.Flow

interface GetMediaListWithNotes {
    operator fun invoke(): Flow<List<MediaAndNotes>>
}
