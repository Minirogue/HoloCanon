package com.holocanon.library.filters.test.bindings.fakes

import filters.GetAllFilterGroups
import filters.model.FilterGroup
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeGetAllFilterGroups @Inject constructor() : GetAllFilterGroups {
    override fun invoke(): Flow<Map<FilterGroup, List<MediaFilter>>> = getFakeFilterGroups()
}
