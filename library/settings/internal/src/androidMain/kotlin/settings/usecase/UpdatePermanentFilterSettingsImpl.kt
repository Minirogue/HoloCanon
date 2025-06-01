package settings.usecase

import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import settings.data.SettingsRepo

@Inject
@ContributesBinding(AppScope::class)
class UpdatePermanentFilterSettingsImpl constructor(
    private val settingsRepo: SettingsRepo,
) : UpdatePermanentFilterSettings {
    override suspend fun invoke(mediaType: MediaType, isActive: Boolean) {
        settingsRepo.updatePermanentFilter(mediaType, isActive)
    }
}
