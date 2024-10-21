package filters

import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetFiltersOfType {
    operator fun invoke(filterType: FilterType): Flow<List<MediaFilter>>
}
