package com.holocanon.library.filters.test.bindings.fakes

import filters.UpdateFilter
import filters.model.FilterGroup
import filters.model.MediaFilter
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FakeUpdateFilter @Inject constructor() : UpdateFilter {
    override suspend fun invoke(mediaFilter: MediaFilter) {
        updateFakeFilters { list ->
            list.map {
                if (it.id == mediaFilter.id) mediaFilter else it
            }
        }
    }

    override suspend fun invoke(filterGroup: FilterGroup) {
        updateFilterGroups { list ->
            list.map {
                if (it.type == filterGroup.type) filterGroup else it
            }
        }
    }
}
