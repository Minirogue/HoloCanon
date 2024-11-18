package filters

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetActiveFiltersImpl @Inject constructor(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
) : GetActiveFilters {
    override fun invoke(): Flow<List<MediaFilter>> = combine(
        daoFilter.getActiveFilters(),
        getPermanentFilters(),
    ){ activeFilters, permanentFilters ->
        activeFilters.map { fullFilter -> fullFilter.toMediaFilter() }
            .filter { mediaFilter -> mediaFilter !in permanentFilters }
    }
}
