package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto

interface UpdateNotes {
    operator fun invoke(mediaNotesDto: MediaNotesDto?)
}
