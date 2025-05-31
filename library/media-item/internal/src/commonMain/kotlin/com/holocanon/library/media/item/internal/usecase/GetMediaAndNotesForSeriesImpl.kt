package com.holocanon.library.media.item.internal.usecase

import com.holocanon.library.media.item.model.MediaAndNotes
import com.holocanon.library.media.item.usecase.GetMediaAndNotesForSeries
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.usecase.AdaptMediaItemDtoToStarWarsMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetMediaAndNotesForSeriesImpl @Inject constructor(
    private val daoMedia: DaoMedia,
    private val adaptMediaItemDtoToStarWarsMedia: AdaptMediaItemDtoToStarWarsMedia,
) : GetMediaAndNotesForSeries {
    override fun invoke(seriesId: Int): Flow<List<MediaAndNotes>> {
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
