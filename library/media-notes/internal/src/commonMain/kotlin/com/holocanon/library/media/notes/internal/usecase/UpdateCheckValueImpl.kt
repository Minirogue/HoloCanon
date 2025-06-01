package com.holocanon.library.media.notes.internal.usecase

import com.holocanon.core.data.dao.DaoMedia
import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.usecase.UpdateCheckValue
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UpdateCheckValueImpl(
    private val daoMedia: com.holocanon.core.data.dao.DaoMedia,
) : UpdateCheckValue {
    override suspend fun invoke(checkbox: CheckBoxNumber, mediaItemId: Long, newValue: Boolean) {
        daoMedia.updateMediaNote(checkbox, mediaItemId, newValue)
    }
}
