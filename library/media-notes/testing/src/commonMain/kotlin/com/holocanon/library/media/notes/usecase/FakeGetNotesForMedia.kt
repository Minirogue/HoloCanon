package com.holocanon.library.media.notes.usecase

import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.media.notes.usecase.GetNotesForMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetNotesForMedia : GetNotesForMedia {
    private val returnFlow = MutableSharedFlow<MediaNotes>(replay = Int.MAX_VALUE)

    suspend fun emit(notes: MediaNotes) {
        returnFlow.emit(notes)
    }

    override fun invoke(mediaId: Long): Flow<MediaNotes> = returnFlow
}
