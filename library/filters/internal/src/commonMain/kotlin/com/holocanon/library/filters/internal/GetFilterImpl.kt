package com.holocanon.library.filters.internal

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.GetFilter
import filters.model.MediaFilter
@Inject
@ContributesBinding(AppScope::class)
class GetFilterImpl(private val daoFilter: DaoFilter) : GetFilter {
    override suspend operator fun invoke(id: Int, typeId: Int): MediaFilter? = daoFilter.getFilter(
        id,
        typeId,
    )?.toMediaFilter()
}
