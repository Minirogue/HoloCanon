package filters

import kotlinx.coroutines.flow.Flow

interface GetFiltersOfType {
    operator fun invoke(filterType: FilterType): Flow<List<MediaFilter>>
}
