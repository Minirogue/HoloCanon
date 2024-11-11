package com.holocanon.library.filters.test.bindings.fakes

import filters.GetFiltersOfType
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeGetFiltersOfType @Inject constructor() : GetFiltersOfType {
    override fun invoke(filterType: FilterType): Flow<List<MediaFilter>> = fakeFilters.map { list ->
        list.filter { it.filterType == filterType }
    }
}
