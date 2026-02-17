package com.holocanon.library.filters.testing.fakes

import filters.GetPermanentFilters
import filters.model.MediaFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGetPermanentFilters : GetPermanentFilters {

    private var flow: Flow<List<MediaFilter>> = flowOf(emptyList())

    fun setPermanentFilters(value: Flow<List<MediaFilter>>) {
        flow = value
    }

    override fun invoke(): Flow<List<MediaFilter>> = flow
}
