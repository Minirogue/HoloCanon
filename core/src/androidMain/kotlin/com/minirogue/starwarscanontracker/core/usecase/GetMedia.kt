package com.minirogue.starwarscanontracker.core.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import javax.inject.Inject

class GetMedia @Inject constructor(private val daoMedia: DaoMedia) {
    /**
     * Returns LiveData containing the MediaItem corresponding to the given id.
     *
     * @param itemId the id for the desired MediaItem
     */
    operator fun invoke(itemId: Int): LiveData<MediaItemDto> {
        return daoMedia.getMediaItemById(itemId)
    }
}
