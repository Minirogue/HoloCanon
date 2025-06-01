package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.UpdateFilter
import filters.model.FilterType
import filters.model.MediaFilter
import settings.usecase.FlipIsCheckboxActive

@Inject
@ContributesBinding(AppScope::class)
class FlipIsCheckboxActiveImpl(
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
