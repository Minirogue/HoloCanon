package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateNotes @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Update a MediaNotes entry in the room.
     *
     * @param mediaNotes the MediaNotes object to be updated
     */
    operator fun invoke(mediaNotes: MediaNotes?) {
        if (mediaNotes != null) {
            GlobalScope.launch(Dispatchers.Default) { daoMedia.update(mediaNotes) }
        }
    }
}
