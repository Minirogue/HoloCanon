package com.minirogue.starwarscanontracker.core.usecase

import android.content.Context
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import javax.inject.Inject

class GetAllMediaTypesUseCase @Inject constructor() {

    /**
     * WARNING: THIS IS NOT THREAD SAFE. TODO this and its usages need to be replaced with coroutines
     */
    operator fun invoke(context: Context): List<MediaType> =
        MediaDatabase.getMediaDataBase(context).daoType.allNonLive
}
