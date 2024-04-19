package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.usecase.UpdateNotes
import javax.inject.Inject

class UpdateNotesImpl @Inject constructor(private val daoMedia: DaoMedia) : UpdateNotes {
    /**
     * Update a MediaNotes entry in the room.
     *
     * @param mediaNotesDto the MediaNotes object to be updated
     */
    override suspend fun invoke(mediaNotesDto: MediaNotesDto?) {
        if (mediaNotesDto != null) {
            daoMedia.update(mediaNotesDto)
        }
    }
}
