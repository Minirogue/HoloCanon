package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import settings.data.SettingsRepo
import settings.model.AllSettings

@Inject
@ContributesBinding(AppScope::class)
class GetAllSettingsImpl constructor(private val settingsRepo: SettingsRepo) : GetAllSettings {
    override fun invoke(): Flow<AllSettings> = settingsRepo.getSettings()
}
