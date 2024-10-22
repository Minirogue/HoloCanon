package filters

import filters.model.MediaFilter

interface GetPermanentFilters {
    suspend operator fun invoke(): List<MediaFilter>
}
