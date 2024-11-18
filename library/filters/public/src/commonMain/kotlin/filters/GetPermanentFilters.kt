package filters

import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetPermanentFilters {
    operator fun invoke(): Flow<List<MediaFilter>>
}
