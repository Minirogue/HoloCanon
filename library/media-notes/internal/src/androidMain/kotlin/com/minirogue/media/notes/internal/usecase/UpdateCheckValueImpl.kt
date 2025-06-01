package com.minirogue.media.notes.internal.usecase

import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.usecase.UpdateCheckValue
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UpdateCheckValueImpl(
    private val daoMedia: DaoMedia,
) : UpdateCheckValue {
    override suspend fun invoke(checkbox: CheckBoxNumber, mediaItemId: Long, newValue: Boolean) {
        daoMedia.updateMediaNote(checkbox, mediaItemId, newValue)
    }
}
