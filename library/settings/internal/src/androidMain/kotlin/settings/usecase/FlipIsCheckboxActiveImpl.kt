package settings.usecase

import filters.UpdateFilter
import filters.model.FilterType
import filters.model.MediaFilter
import settings.data.SettingsRepo
import javax.inject.Inject

internal class FlipIsCheckboxActiveImpl @Inject constructor(
    private val settingsRepo: SettingsRepo,
    private val updateFilter: UpdateFilter,
) : FlipIsCheckboxActive {
    override suspend fun invoke(whichBox: Int) {
        val newSetting =
            settingsRepo.updateCheckbox(whichBox = whichBox) { it.copy(isInUse = !it.isInUse) }
                .getOrNull()
        if (newSetting?.isInUse == false) {
            val filter = MediaFilter(
                id = whichBox,
                name = newSetting.name,
                filterType = FilterType.Checkbox,
                isPositive = true,
                isActive = false,
            )
            updateFilter(filter)
        }
    }
}
