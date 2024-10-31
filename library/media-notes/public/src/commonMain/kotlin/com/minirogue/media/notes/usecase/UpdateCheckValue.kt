package com.minirogue.media.notes.usecase

import com.minirogue.media.notes.model.CheckBoxNumber

interface UpdateCheckValue {
    suspend operator fun invoke(checkbox: CheckBoxNumber, mediaItemId: Long, newValue: Boolean)
}
