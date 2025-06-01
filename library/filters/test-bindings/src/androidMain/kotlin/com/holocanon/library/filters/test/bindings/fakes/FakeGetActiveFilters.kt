package com.holocanon.library.filters.test.bindings.fakes

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import filters.GetActiveFilters
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class FakeGetActiveFilters : GetActiveFilters {
    override fun invoke(): Flow<List<MediaFilter>> =
        getFakeFilters().map { list -> list.filter { it.isActive } }
}
