package com.holocanon.library.filters.testing.fakes

import filters.GetPermanentFilters
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow

class FakeGetPermanentFilters(
    private val flow: Flow<List<MediaFilter>>,
) : GetPermanentFilters {
    override fun invoke(): Flow<List<MediaFilter>> = flow
}
