package settings.usecase

import com.minirogue.api.media.MediaType
import kotlinx.coroutines.flow.Flow

interface GetPermanentFilterSettings {
    operator fun invoke(): Flow<Map<MediaType, Boolean>>
}
