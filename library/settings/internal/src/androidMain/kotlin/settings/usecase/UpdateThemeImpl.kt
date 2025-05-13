package settings.usecase

import settings.data.SettingsRepo
import settings.model.DarkModeSetting
import javax.inject.Inject

internal class UpdateThemeImpl @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateTheme {
    override suspend fun invoke(newTheme: DarkModeSetting) {
        settingsRepo.updateDarkModeSetting(newTheme)
    }
}