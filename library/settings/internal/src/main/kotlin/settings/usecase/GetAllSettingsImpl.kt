package settings.usecase

import kotlinx.coroutines.flow.Flow
import settings.data.SettingsRepo
import settings.model.AllSettings
import javax.inject.Inject

internal class GetAllSettingsImpl @Inject constructor(private val settingsRepo: SettingsRepo) : GetAllSettings {
    override fun invoke(): Flow<AllSettings> = settingsRepo.getSettings()
}
