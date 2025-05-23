package com.holocanon.library.filters.test.bindings.fakes

import filters.GetActiveFilters
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeGetActiveFilters @Inject constructor() : GetActiveFilters {
    override fun invoke(): Flow<List<MediaFilter>> =
        getFakeFilters().map { list -> list.filter { it.isActive } }
}
