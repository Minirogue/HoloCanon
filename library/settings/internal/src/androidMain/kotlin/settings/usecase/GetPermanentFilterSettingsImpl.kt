package settings.usecase

import com.minirogue.common.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.data.SettingsRepo
import javax.inject.Inject

internal class GetPermanentFilterSettingsImpl @Inject constructor(
    private val settingsRepo: SettingsRepo,
) : GetPermanentFilterSettings {
    override fun invoke(): Flow<Map<MediaType, Boolean>> {
        return settingsRepo.getSettings().map { it.permanentFilterSettings }
    }
}
