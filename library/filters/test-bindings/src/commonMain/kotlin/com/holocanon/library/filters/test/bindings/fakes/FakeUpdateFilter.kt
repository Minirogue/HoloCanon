package com.holocanon.library.filters.test.bindings.fakes

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import filters.UpdateFilter
import filters.model.FilterGroup
import filters.model.MediaFilter
@Inject
@ContributesBinding(AppScope::class)
class FakeUpdateFilter : UpdateFilter {
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
