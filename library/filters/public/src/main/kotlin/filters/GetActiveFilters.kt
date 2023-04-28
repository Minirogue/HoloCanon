package filters

import kotlinx.coroutines.flow.Flow

interface GetActiveFilters {
    operator fun invoke(): Flow<List<MediaFilter>>
}