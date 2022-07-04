package com.minirogue.starwarscanontracker.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import javax.inject.Inject

class GetNotesForMedia @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    operator fun invoke(itemId: Int): LiveData<MediaNotes> {
        return daoMedia.getMediaNotesById(itemId)
    }
}
