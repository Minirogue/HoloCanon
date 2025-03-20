package com.holocanon.library.settings.test.bindings.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

class FakeGetCheckboxSettings @Inject constructor() : GetCheckboxSettings {
    private val returnFlow = MutableStateFlow(
        CheckboxSettings(
            CheckboxSetting("checkbox1", true),
            CheckboxSetting("checkbox2", false),
            CheckboxSetting("checkbox3", true),
        ),
    )

    override fun invoke(): Flow<CheckboxSettings> {
        return returnFlow
    }
}
