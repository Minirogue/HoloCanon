package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.usecase.SetLatestDatabaseVersion

@Inject
@ContributesBinding(AppScope::class)
class SetLatestDatabaseVersionImpl constructor(
    private val settingsRepo: SettingsRepo,
) : SetLatestDatabaseVersion {
    override suspend fun invoke(newVersionNumber: Long) {
        settingsRepo.updateDatabaseVersionNumber(newVersionNumber)
    }
}
