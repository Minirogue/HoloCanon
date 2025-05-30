package com.minirogue.starwarscanontracker.core.usecase

import com.holocanon.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMediaAndNotesForSeries @Inject constructor(
    private val daoMedia: DaoMedia,
    private val adaptMediaItemDtoToStarWarsMedia: AdaptMediaItemDtoToStarWarsMedia,
) {
    operator fun invoke(seriesId: Int): Flow<List<MediaAndNotes>> {
        return daoMedia.getMediaAndNotesForSeries(seriesId).map { list ->
            list.map {
                MediaAndNotes(
                    adaptMediaItemDtoToStarWarsMedia(it.mediaItemDto),
                    it.mediaNotesDto.toMediaNotes(),
                )
            }
        }
    }
}
