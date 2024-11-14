package filters

import filters.model.FilterGroup
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

interface GetAllFilterGroups {
    operator fun invoke(): Flow<Map<FilterGroup, List<MediaFilter>>>
}
