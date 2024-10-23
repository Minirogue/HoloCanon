package settings.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.data.SettingsRepo
import settings.model.CheckboxSettings
import javax.inject.Inject

internal class GetCheckboxSettingsImpl @Inject constructor(private val settingsRepo: SettingsRepo): GetCheckboxSettings {
    override fun invoke(): Flow<CheckboxSettings> = settingsRepo.getSettings().map { it.checkboxSettings }
}
