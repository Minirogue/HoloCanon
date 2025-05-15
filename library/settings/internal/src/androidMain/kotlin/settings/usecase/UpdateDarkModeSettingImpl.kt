package settings.usecase

import settings.data.SettingsRepo
import settings.model.DarkModeSetting
import javax.inject.Inject

internal class UpdateDarkModeSettingImpl @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateDarkModeSetting {
    override suspend fun invoke(newDarkModeSetting: DarkModeSetting) {
        settingsRepo.updateDarkModeSetting(newDarkModeSetting)
    }
}
