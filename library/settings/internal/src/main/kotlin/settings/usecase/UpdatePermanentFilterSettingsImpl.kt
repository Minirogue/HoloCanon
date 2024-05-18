package settings.usecase

import com.minirogue.common.model.MediaType
import settings.data.SettingsRepo
import javax.inject.Inject

internal class UpdatePermanentFilterSettingsImpl @Inject constructor(
    private val settingsRepo: SettingsRepo
) : UpdatePermanentFilterSettings {
    override suspend fun invoke(mediaType: MediaType, isActive: Boolean) {
        settingsRepo.updatePermanentFilter(mediaType, isActive)
    }
}
