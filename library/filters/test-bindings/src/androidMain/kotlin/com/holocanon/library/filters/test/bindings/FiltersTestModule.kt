package com.holocanon.library.filters.test.bindings

import com.holocanon.library.filters.test.bindings.fakes.FakeGetActiveFilters
import com.holocanon.library.filters.test.bindings.fakes.FakeGetAllFilterGroups
import com.holocanon.library.filters.test.bindings.fakes.FakeUpdateFilter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import filters.GetActiveFilters
import filters.GetAllFilterGroups
import filters.UpdateFilter

@Module
@InstallIn(SingletonComponent::class)
interface FiltersTestModule {
    @Binds
    fun bindGetActiveFilters(impl: FakeGetActiveFilters): GetActiveFilters

    @Binds
    fun bindGetAllFilterTypes(impl: FakeGetAllFilterGroups): GetAllFilterGroups

    @Binds
    fun bindUpdateFilter(impl: FakeUpdateFilter): UpdateFilter
}
