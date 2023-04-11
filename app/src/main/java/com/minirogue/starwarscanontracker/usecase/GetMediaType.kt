package com.minirogue.starwarscanontracker.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaTypeDto
import javax.inject.Inject

class GetMediaType @Inject constructor(private val daoType: DaoType) {
    operator fun invoke(itemId: Int): LiveData<MediaTypeDto?> = daoType.getLiveMediaType(itemId)
}
