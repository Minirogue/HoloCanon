package com.holocanon.library.media.notes.usecase

import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.usecase.UpdateCheckValue
import kotlin.test.assertEquals

class FakeUpdateCheckValue : UpdateCheckValue {
    private var invokedCheckBoxNumber: CheckBoxNumber? = null
    private var invokedMediaItemId: Long? = null
    private var invokedNewValue: Boolean? = null

    fun assertInvoked(
        expectedCheckboxNumber: CheckBoxNumber,
        expectedMediaId: Long,
        expectedNewValue: Boolean,
    ) {
        assertEquals(
            expectedCheckboxNumber,
            invokedCheckBoxNumber,
            "UpdateCheckValue invoked with unexpected CheckBoxNumber",
        )
        assertEquals(
            expectedMediaId,
            invokedMediaItemId,
            "UpdateCheckValue invoked with unexpected media Id",
        )
        assertEquals(
            expectedNewValue,
            invokedNewValue,
            "UpdateCheckValue invoked with unexpected new value",
        )
    }

    override suspend fun invoke(
        checkbox: CheckBoxNumber,
        mediaItemId: Long,
        newValue: Boolean,
    ) {
        invokedCheckBoxNumber = checkbox
        invokedNewValue = newValue
        invokedMediaItemId = mediaItemId
    }
}
