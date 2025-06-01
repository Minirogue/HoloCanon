package com.holocanon.library.media.item.internal.usecase

import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.usecase.AdaptMediaItemDtoToStarWarsMedia
import com.holocanon.library.media.item.usecase.GetMedia
import com.minirogue.common.model.StarWarsMedia
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
class GetMediaImpl(
    private val daoMedia: com.holocanon.core.data.dao.DaoMedia,
    private val adaptMediaItemDtoToStarWarsMedia: AdaptMediaItemDtoToStarWarsMedia,
) : GetMedia {
    override fun invoke(mediaId: Long): Flow<StarWarsMedia> {
        return daoMedia.getMediaItemById(mediaId).map {
            adaptMediaItemDtoToStarWarsMedia(it)
        }
    }
}
