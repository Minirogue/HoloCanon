package com.minirogue.media.notes.internal.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.usecase.GetNotesForMedia
import javax.inject.Inject

class GetNotesForMediaImpl @Inject constructor(private val daoMedia: DaoMedia) : GetNotesForMedia {
    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    override fun invoke(itemId: Int): LiveData<MediaNotesDto> {
        return daoMedia.getMediaNotesById(itemId)
    }
}
