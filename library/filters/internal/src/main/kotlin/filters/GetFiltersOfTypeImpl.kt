package filters

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetFiltersOfTypeImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
) : GetFiltersOfType {
    override operator fun invoke(filterType: filters.FilterType): Flow<List<MediaFilter>> = daoFilter
        .getFiltersWithType(filterType.legacyIntegerConversion)
        .map {
            val mediaFilters = it.map { fullFilter -> fullFilter.toMediaFilter() }
            if (filterType.legacyIntegerConversion == FilterType.FILTERCOLUMN_TYPE) {
                mediaFilters.filter { mediaFilter -> mediaFilter !in getPermanentFilters() }
            } else mediaFilters
        }
}
