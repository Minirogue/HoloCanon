package com.holocanon.core.usecase

import androidx.room.RoomRawQuery
import com.holocanon.core.data.dao.DaoMedia
import dev.zacsweers.metro.Inject

@Inject
class QueryMedia internal constructor(private val daoMedia: DaoMedia) {
    fun query(rawQuery: String) = daoMedia.getMediaAndNotesRawQuery(RoomRawQuery(rawQuery))
}
