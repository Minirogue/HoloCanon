package com.holocanon.library.media.item.internal.usecase

import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.library.media.item.model.MediaAndNotes
import com.holocanon.library.media.item.usecase.GetMediaAndNotesForSeries
import com.minirogue.starwarscanontracker.core.usecase.AdaptMediaItemDtoToStarWarsMedia
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
class GetMediaAndNotesForSeriesImpl(
    private val daoMedia: com.holocanon.core.data.dao.DaoMedia,
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
