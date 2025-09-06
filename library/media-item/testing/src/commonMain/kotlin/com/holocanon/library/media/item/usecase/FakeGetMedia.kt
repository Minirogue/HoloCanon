package com.holocanon.library.media.item.usecase

import com.minirogue.common.model.StarWarsMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetMedia : GetMedia {
    private val mediaFlow = MutableSharedFlow<StarWarsMedia>(replay = Int.MAX_VALUE)

    suspend fun emit(media: StarWarsMedia) {
        mediaFlow.emit(media)
    }

    override fun invoke(mediaId: Long): Flow<StarWarsMedia> = mediaFlow
}
