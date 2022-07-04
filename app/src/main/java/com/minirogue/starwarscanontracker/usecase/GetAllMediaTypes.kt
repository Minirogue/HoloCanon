package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class GetAllMediaTypes @Inject constructor(private val daoType: DaoType) {
    suspend operator fun invoke(): List<MediaType> = daoType.allNonLive()
}
