package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import settings.model.AllSettings
import settings.usecase.GetAllSettings

@Inject
@ContributesBinding(AppScope::class)
class GetAllSettingsImpl(private val settingsRepo: SettingsRepo) : GetAllSettings {
    override fun invoke(): Flow<AllSettings> = settingsRepo.getSettings()
}
