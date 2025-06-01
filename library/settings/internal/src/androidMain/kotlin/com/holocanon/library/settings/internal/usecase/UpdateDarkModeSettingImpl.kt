package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.model.DarkModeSetting
import settings.usecase.UpdateDarkModeSetting

@Inject
@ContributesBinding(AppScope::class)
class UpdateDarkModeSettingImpl constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateDarkModeSetting {
    override suspend fun invoke(newDarkModeSetting: DarkModeSetting) {
        settingsRepo.updateDarkModeSetting(newDarkModeSetting)
    }
}
