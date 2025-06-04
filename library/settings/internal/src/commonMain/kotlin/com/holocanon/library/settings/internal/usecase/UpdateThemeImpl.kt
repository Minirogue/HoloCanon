package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.model.Theme
import settings.usecase.UpdateTheme

@Inject
@ContributesBinding(AppScope::class)
class UpdateThemeImpl(
    private val settingsRepo: SettingsRepo,
) : UpdateTheme {
    override suspend fun invoke(newTheme: Theme) {
        settingsRepo.updateTheme(newTheme)
    }
}
