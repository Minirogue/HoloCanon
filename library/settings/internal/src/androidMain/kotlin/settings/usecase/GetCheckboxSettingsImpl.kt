package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.data.SettingsRepo
import settings.model.CheckboxSettings

@Inject
@ContributesBinding(AppScope::class)
class GetCheckboxSettingsImpl constructor(private val settingsRepo: SettingsRepo) : GetCheckboxSettings {
    override fun invoke(): Flow<CheckboxSettings> = settingsRepo.getSettings().map { it.checkboxSettings }
}
