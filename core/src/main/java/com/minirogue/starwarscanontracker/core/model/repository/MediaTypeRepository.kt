package com.minirogue.starwarscanontracker.core.model.repository

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class MediaTypeRepository @Inject constructor(private val mediaTypeDao: DaoType) {

    /**
     * TODO this should be replaced with a coroutine version of this function.
     */
    fun getAllTypes(): List<MediaType> = mediaTypeDao.allNonLive
}
