package filters

import filters.model.FilterGroup
import kotlinx.coroutines.flow.Flow

interface GetAllFilterTypes {
    operator fun invoke(): Flow<List<FilterGroup>>
}
