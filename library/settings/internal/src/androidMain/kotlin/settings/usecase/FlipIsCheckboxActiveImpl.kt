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
                id = 1,
                name = newSetting.name,
                filterType = when (whichBox) {
                    1 -> FilterType.CheckboxOne
                    2 -> FilterType.CheckboxTwo
                    3 -> FilterType.CheckboxThree
                    else -> error("whickBox parameter of UpdateCheckboxActive must be 1, 2, or 3")
                },
                isPositive = true,
                isActive = false,
            )
            updateFilter(filter)
        }
    }
}
