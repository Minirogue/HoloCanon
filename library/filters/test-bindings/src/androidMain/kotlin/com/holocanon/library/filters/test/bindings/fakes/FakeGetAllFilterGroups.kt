package com.holocanon.library.filters.test.bindings.fakes

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.GetAllFilterGroups
import filters.model.FilterGroup
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
@Inject
@ContributesBinding(AppScope::class)
class FakeGetAllFilterGroups : GetAllFilterGroups {
    override fun invoke(): Flow<Map<FilterGroup, List<MediaFilter>>> = getFakeFilterGroups()
}
