package settings.usecase

import settings.data.SettingsRepo
import javax.inject.Inject

internal class UpdateCheckboxNameImpl @Inject constructor(
    private val settingsRepo: SettingsRepo
) : UpdateCheckboxName {
    override suspend fun invoke(whichBox: Int, newName: String) {
        settingsRepo.updateCheckbox(whichBox = whichBox) { it.copy(name = newName) }
    }
}
