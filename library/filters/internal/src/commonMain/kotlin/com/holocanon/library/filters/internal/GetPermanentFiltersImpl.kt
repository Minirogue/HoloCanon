package com.holocanon.library.filters.internal

import com.holocanon.core.data.dao.DaoFilter
import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.GetPermanentFilters
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import settings.usecase.GetPermanentFilterSettings

@Inject
@ContributesBinding(AppScope::class)
class GetPermanentFiltersImpl(
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
