package com.minirogue.starwarscanontracker.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import javax.inject.Inject

class GetNotesBySeries @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Returns LiveData containing a List of MediaItems belonging to the Series with seriesId
     */
    operator fun invoke(seriesId: Int): LiveData<List<MediaNotes>> {
        return daoMedia.getMediaNotesBySeries(seriesId)
    }
}