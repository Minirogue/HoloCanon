package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.usecase.UpdateWifiSetting

@Inject
@ContributesBinding(AppScope::class)
class UpdateWifiSettingImpl(private val settingsRepo: SettingsRepo) : UpdateWifiSetting {
    override suspend fun invoke(newValue: Boolean) {
        settingsRepo.updateWifiSetting(newValue)
    }
}
