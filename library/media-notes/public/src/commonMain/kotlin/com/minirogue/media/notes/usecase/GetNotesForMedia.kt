package com.minirogue.media.notes.usecase

import com.minirogue.media.notes.model.MediaNotes
import kotlinx.coroutines.flow.Flow

interface GetNotesForMedia {
    operator fun invoke(mediaId: Int): Flow<MediaNotes>
}
