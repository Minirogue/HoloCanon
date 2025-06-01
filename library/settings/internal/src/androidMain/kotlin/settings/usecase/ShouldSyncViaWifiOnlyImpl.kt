package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.data.SettingsRepo
@Inject
@ContributesBinding(AppScope::class)
class ShouldSyncViaWifiOnlyImpl(private val settingsRepo: SettingsRepo) : ShouldSyncViaWifiOnly {
    override fun invoke(): Flow<Boolean> = settingsRepo.getSettings().map { it.syncWifiOnly }
}
