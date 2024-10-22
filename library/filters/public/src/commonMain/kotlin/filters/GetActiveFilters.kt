package filters

import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetActiveFilters {
    operator fun invoke(): Flow<List<MediaFilter>>
}
