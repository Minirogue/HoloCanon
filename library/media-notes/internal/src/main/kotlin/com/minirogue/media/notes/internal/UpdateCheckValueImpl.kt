package com.minirogue.media.notes.internal

import com.minirogue.media.notes.CheckBoxNumber
import com.minirogue.media.notes.UpdateCheckValue
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import javax.inject.Inject

internal class UpdateCheckValueImpl @Inject constructor(private val daoMedia: DaoMedia) : UpdateCheckValue {
    override suspend fun invoke(checkbox: CheckBoxNumber, mediaItemId: Long, newValue: Boolean) {
        daoMedia.updateMediaNote(checkbox, mediaItemId, newValue)
    }
}