package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo
import settings.model.Theme

@Inject
@ContributesBinding(AppScope::class)
class UpdateThemeImpl(
    private val settingsRepo: SettingsRepo,
) : UpdateTheme {
    override suspend fun invoke(newTheme: Theme) {
        settingsRepo.updateTheme(newTheme)
    }
}
