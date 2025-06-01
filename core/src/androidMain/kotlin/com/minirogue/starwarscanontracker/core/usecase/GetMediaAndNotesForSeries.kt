package com.minirogue.starwarscanontracker.core.usecase

import com.holocanon.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class GetMediaAndNotesForSeries(
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
