package filters

import com.minirogue.common.model.MediaType
import com.minirogue.common.model.MediaType.Companion.getFromLegacyId
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import settings.usecase.GetPermanentFilterSettings
import javax.inject.Inject

class GetPermanentFiltersImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilterSettings: GetPermanentFilterSettings,
) : GetPermanentFilters {
    override operator fun invoke(): Flow<List<MediaFilter>> = combine(
        getPermanentFilterSettings(),
        daoFilter.getAllFilters(),
    ) { typeMap, allFilters ->
        allFilters.map { it.toMediaFilter() }
            .filter { mediaFilter ->
                mediaFilter.filterType == FilterType.MediaType &&
                    typeMap[MediaType.getFromLegacyId(mediaFilter.id)] == false
            }
    }
}
