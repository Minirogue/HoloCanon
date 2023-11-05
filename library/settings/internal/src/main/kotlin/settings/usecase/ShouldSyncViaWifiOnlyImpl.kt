package settings.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.data.SettingsRepo
import javax.inject.Inject

internal class ShouldSyncViaWifiOnlyImpl @Inject constructor(private val settingsRepo: SettingsRepo) : ShouldSyncViaWifiOnly {
    override fun invoke(): Flow<Boolean> = settingsRepo.getSettings().map { it.syncWifiOnly }
}
