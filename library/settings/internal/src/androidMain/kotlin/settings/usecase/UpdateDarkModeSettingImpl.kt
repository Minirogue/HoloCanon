package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo
import settings.model.DarkModeSetting

@Inject
@ContributesBinding(AppScope::class)
class UpdateDarkModeSettingImpl constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateDarkModeSetting {
    override suspend fun invoke(newDarkModeSetting: DarkModeSetting) {
        settingsRepo.updateDarkModeSetting(newDarkModeSetting)
    }
}
