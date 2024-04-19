package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMediaAndNotesForSeries @Inject constructor(
    private val daoMedia: DaoMedia,
    private val daoCompany: DaoCompany,
    private val daoSeries: DaoSeries,
    private val adaptMediaItemDtoToStarWarsMedia: AdaptMediaItemDtoToStarWarsMedia,
) {
    operator fun invoke(seriesId: Int): Flow<List<MediaAndNotes>> {
        return daoMedia.getMediaAndNotesForSeries(seriesId).map { list ->
            list.map {
                MediaAndNotes(
                    adaptMediaItemDtoToStarWarsMedia(it.mediaItemDto),
                    it.mediaNotesDto.toMediaNotes()
                )
            }
        }
    }
}
