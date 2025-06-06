package com.holocanon.library.settings.internal.usecase

import com.holocanon.library.settings.internal.data.SettingsRepo
import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.GetPermanentFilterSettings

@Inject
@ContributesBinding(AppScope::class)
class GetPermanentFilterSettingsImpl constructor(
    private val settingsRepo: SettingsRepo,
) : GetPermanentFilterSettings {
    override fun invoke(): Flow<Map<MediaType, Boolean>> {
        return settingsRepo.getSettings().map { it.permanentFilterSettings }
    }
}
