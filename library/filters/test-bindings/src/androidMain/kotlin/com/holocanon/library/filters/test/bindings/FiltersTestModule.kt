package com.holocanon.library.filters.test.bindings

import com.holocanon.library.filters.test.bindings.fakes.FakeGetActiveFilters
import com.holocanon.library.filters.test.bindings.fakes.FakeGetAllFilterTypes
import com.holocanon.library.filters.test.bindings.fakes.FakeGetFiltersOfType
import com.holocanon.library.filters.test.bindings.fakes.FakeUpdateFilter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import filters.GetActiveFilters
import filters.GetAllFilterTypes
import filters.GetFiltersOfType
import filters.UpdateFilter

@Module
@InstallIn(SingletonComponent::class)
interface FiltersTestModule {
    @Binds
    fun bindGetActiveFilters(impl: FakeGetActiveFilters): GetActiveFilters

    @Binds
    fun bindgetAllFilterTypes(impl: FakeGetAllFilterTypes): GetAllFilterTypes

    @Binds
    fun bindGetFiltersOfType(impl: FakeGetFiltersOfType): GetFiltersOfType

    @Binds
    fun bindUpdateFilter(impl: FakeUpdateFilter): UpdateFilter
}
