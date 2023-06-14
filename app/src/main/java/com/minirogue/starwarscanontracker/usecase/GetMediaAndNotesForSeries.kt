package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaAndNotesForSeries @Inject constructor(private val daoMedia: DaoMedia) {
    operator fun invoke(seriesId: Int): Flow<List<MediaAndNotes>> {
        return daoMedia.getMediaAndNotesForSeries(seriesId)
    }
}
