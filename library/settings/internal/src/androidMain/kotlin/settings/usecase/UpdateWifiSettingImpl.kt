package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo

@Inject
@ContributesBinding(AppScope::class)
class UpdateWifiSettingImpl(private val settingsRepo: SettingsRepo) : UpdateWifiSetting {
    override suspend fun invoke(newValue: Boolean) {
        settingsRepo.updateWifiSetting(newValue)
    }
}
