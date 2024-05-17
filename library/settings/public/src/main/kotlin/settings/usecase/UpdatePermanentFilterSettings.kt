package settings.usecase

import com.minirogue.common.model.MediaType

interface UpdatePermanentFilterSettings {
    suspend operator fun invoke(mediaType: MediaType, isActive: Boolean)
}
