package com.holocanon.library.media.item.internal.usecase

import com.holocanon.library.media.item.usecase.GetMedia
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.usecase.AdaptMediaItemDtoToStarWarsMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetMediaImpl @Inject constructor(
    private val daoMedia: DaoMedia,
    private val adaptMediaItemDtoToStarWarsMedia: AdaptMediaItemDtoToStarWarsMedia,
) : GetMedia {
    override fun invoke(mediaId: Long): Flow<StarWarsMedia> {
        return daoMedia.getMediaItemById(mediaId).map {
            adaptMediaItemDtoToStarWarsMedia(it)
        }
    }
}
