package com.holocanon.library.settings.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import settings.model.CheckboxSettings

class FakeGetCheckboxSettings : GetCheckboxSettings {
    private var returnFlow = MutableSharedFlow<CheckboxSettings>(replay = Int.MAX_VALUE)
    suspend fun emit(checkboxSettings: CheckboxSettings) {
        returnFlow.emit(checkboxSettings)
    }

    override fun invoke(): Flow<CheckboxSettings> {
        return returnFlow
    }
}
