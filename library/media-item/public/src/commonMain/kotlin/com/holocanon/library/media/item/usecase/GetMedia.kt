package com.holocanon.library.media.item.usecase

import com.minirogue.common.model.StarWarsMedia
import kotlinx.coroutines.flow.Flow

interface GetMedia {
    operator fun invoke(mediaId: Int): Flow<StarWarsMedia>
}
