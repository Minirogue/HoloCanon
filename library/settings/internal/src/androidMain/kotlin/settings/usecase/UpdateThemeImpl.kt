package settings.usecase

import settings.data.SettingsRepo
import settings.model.Theme
import javax.inject.Inject

internal class UpdateThemeImpl @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateTheme {
    override suspend fun invoke(newTheme: Theme) {
        settingsRepo.updateTheme(newTheme)
    }
}
