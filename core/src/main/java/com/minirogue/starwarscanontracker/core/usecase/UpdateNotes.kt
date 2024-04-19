package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto

interface UpdateNotes {
    operator suspend fun invoke(mediaNotesDto: MediaNotesDto?)
}
