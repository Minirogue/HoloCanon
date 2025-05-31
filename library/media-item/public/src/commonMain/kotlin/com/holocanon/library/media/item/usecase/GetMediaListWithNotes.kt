package com.holocanon.library.media.item.usecase

import com.holocanon.library.media.item.model.MediaAndNotes
import kotlinx.coroutines.flow.Flow

interface GetMediaListWithNotes {
    operator fun invoke(): Flow<List<MediaAndNotes>>
}
