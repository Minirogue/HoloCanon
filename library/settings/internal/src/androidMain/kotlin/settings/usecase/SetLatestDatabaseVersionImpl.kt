package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo

@Inject
@ContributesBinding(AppScope::class)
class SetLatestDatabaseVersionImpl constructor(
    private val settingsRepo: SettingsRepo,
) : SetLatestDatabaseVersion {
    override suspend fun invoke(newVersionNumber: Long) {
        settingsRepo.updateDatabaseVersionNumber(newVersionNumber)
    }
}
