package filters.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import filters.GetActiveFilters
import filters.GetActiveFiltersImpl
import filters.GetAllFilterGroups
import filters.GetAllFilterGroupsImpl
import filters.GetFilter
import filters.GetFilterImpl
import filters.GetPermanentFilters
import filters.GetPermanentFiltersImpl
import filters.UpdateFilter
import filters.UpdateFilterImpl

@InstallIn(SingletonComponent::class)
@Module
internal interface FilterModule {
    @Binds
    fun bindGetActiveFilters(getActiveFiltersImpl: GetActiveFiltersImpl): GetActiveFilters

    @Binds
    fun bindUpdateFilter(updateFilterImpl: UpdateFilterImpl): UpdateFilter

    @Binds
    fun bindGetFilter(getFilterImpl: GetFilterImpl): GetFilter

    @Binds
    fun bindGetPermanentFilters(getPermanentFiltersImpl: GetPermanentFiltersImpl): GetPermanentFilters

    @Binds
    fun bindGetAllFiltertypes(impl: GetAllFilterGroupsImpl): GetAllFilterGroups
}
