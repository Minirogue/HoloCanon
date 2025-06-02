package com.holocanon.library.media.notes.internal.usecase

import com.holocanon.core.data.dao.DaoMedia
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.media.notes.usecase.GetNotesForMedia
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
class GetNotesForMediaImpl(private val daoMedia: DaoMedia) : GetNotesForMedia {
    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    override fun invoke(mediaId: Long): Flow<MediaNotes> {
        return daoMedia.getMediaNotesById(mediaId).map { it.toMediaNotes() }
    }
}
