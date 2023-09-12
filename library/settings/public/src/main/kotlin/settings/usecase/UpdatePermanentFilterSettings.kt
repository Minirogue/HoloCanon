package settings.usecase

import com.minirogue.api.media.MediaType

interface UpdatePermanentFilterSettings {
    suspend operator fun invoke(mediaType: MediaType, isActive: Boolean)
}