package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import com.holocanon.library.settings.usecase.GetCheckboxSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.model.CheckboxSettings

@Inject
@ContributesBinding(AppScope::class)
class GetCheckboxSettingsImpl(private val settingsRepo: SettingsRepo) : GetCheckboxSettings {
    override fun invoke(): Flow<CheckboxSettings> =
        settingsRepo.getSettings().map { it.checkboxSettings }
}
