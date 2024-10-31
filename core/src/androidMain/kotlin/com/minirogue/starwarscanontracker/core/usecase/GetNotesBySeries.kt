package com.minirogue.starwarscanontracker.core.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesBySeries @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Returns LiveData containing a List of MediaItems belonging to the Series with seriesId
     */
    operator fun invoke(seriesId: Int): Flow<List<MediaNotesDto>> {
        return daoMedia.getMediaNotesBySeries(seriesId)
    }
}
