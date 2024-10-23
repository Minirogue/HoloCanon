package settings.usecase

import settings.data.SettingsRepo
import javax.inject.Inject

internal class UpdateCheckboxActiveImpl @Inject constructor(
    private val settingsRepo: SettingsRepo
) : UpdateCheckboxActive {
    override suspend fun invoke(whichBox: Int, newValue: Boolean) {
        settingsRepo.updateCheckbox(whichBox = whichBox, newUsageValue = newValue)
    }
}
