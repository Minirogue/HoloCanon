package settings.usecase

import settings.data.SettingsRepo
import javax.inject.Inject

internal class UpdateWifiSettingImpl @Inject constructor(private val settingsRepo: SettingsRepo) : UpdateWifiSetting {
    override suspend fun invoke(newValue: Boolean) {
        settingsRepo.updateWifiSetting(newValue)
    }
}
