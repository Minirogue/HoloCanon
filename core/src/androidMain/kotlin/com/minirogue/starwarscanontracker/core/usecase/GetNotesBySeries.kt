package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesBySeries @Inject constructor(private val daoMedia: DaoMedia) {
    operator fun invoke(seriesId: Int): Flow<List<MediaNotesDto>> {
        return daoMedia.getMediaNotesBySeries(seriesId)
    }
}
