package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.ShouldSyncViaWifiOnly

@Inject
@ContributesBinding(AppScope::class)
class ShouldSyncViaWifiOnlyImpl(private val settingsRepo: SettingsRepo) : ShouldSyncViaWifiOnly {
    override fun invoke(): Flow<Boolean> = settingsRepo.getSettings().map { it.syncWifiOnly }
}
