package filters

import kotlinx.coroutines.flow.Flow

interface GetFiltersOfType {
    operator fun invoke(typeId: Int): Flow<List<MediaFilter>>
}
