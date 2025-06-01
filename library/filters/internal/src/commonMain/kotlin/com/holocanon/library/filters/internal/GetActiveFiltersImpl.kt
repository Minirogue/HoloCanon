package com.holocanon.library.filters.internal

import com.holocanon.core.data.dao.DaoFilter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.GetActiveFilters
import filters.GetPermanentFilters
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
@ContributesBinding(AppScope::class)
class GetActiveFiltersImpl(
    private val daoFilter: DaoFilter,
    private val getPermanentFilters: GetPermanentFilters,
) : GetActiveFilters {
    override fun invoke(): Flow<List<MediaFilter>> = combine(
        daoFilter.getActiveFilters(),
        getPermanentFilters(),
    ) { activeFilters, permanentFilters ->
        activeFilters.map { fullFilter -> fullFilter.toMediaFilter() }
            .filter { mediaFilter -> mediaFilter !in permanentFilters }
    }
}
