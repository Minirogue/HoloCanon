package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateNotes @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Update a MediaNotes entry in the room.
     *
     * @param mediaNotesDto the MediaNotes object to be updated
     */
    operator fun invoke(mediaNotesDto: MediaNotesDto?) {
        if (mediaNotesDto != null) {
            GlobalScope.launch(Dispatchers.Default) { daoMedia.update(mediaNotesDto) }
        }
    }
}
