package settings.usecase

import settings.data.SettingsRepo
import javax.inject.Inject

internal class SetLatestDatabaseVersionImpl @Inject constructor(private val settingsRepo: SettingsRepo) : SetLatestDatabaseVersion {
    override suspend fun invoke(newVersionNumber: Long) {
        settingsRepo.updateDatabaseVersionNumber(newVersionNumber)
    }
}
