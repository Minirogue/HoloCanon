package com.minirogue.media.notes.internal.usecase

import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.media.notes.usecase.GetNotesForMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNotesForMediaImpl @Inject constructor(private val daoMedia: DaoMedia) : GetNotesForMedia {
    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    override fun invoke(mediaId: Int): Flow<MediaNotes> {
        return daoMedia.getMediaNotesById(mediaId.toLong()).map { it.toMediaNotes() }
    }
}
