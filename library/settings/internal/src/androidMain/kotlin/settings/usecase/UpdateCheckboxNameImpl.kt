package settings.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo

@Inject
@ContributesBinding(AppScope::class)
class UpdateCheckboxNameImpl constructor(
    private val settingsRepo: SettingsRepo,
) : UpdateCheckboxName {
    override suspend fun invoke(whichBox: Int, newName: String) {
        settingsRepo.updateCheckbox(whichBox = whichBox) { it.copy(name = newName) }
    }
}
