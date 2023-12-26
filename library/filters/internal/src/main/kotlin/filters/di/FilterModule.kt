package filters.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import filters.GetActiveFilters
import filters.GetActiveFiltersImpl
import filters.GetAllFilterTypes
import filters.GetAllFilterTypesImpl
import filters.GetFilter
import filters.GetFilterImpl
import filters.GetFiltersOfType
import filters.GetFiltersOfTypeImpl
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
    fun bindGetFiltersOfType(getFiltersOfTypeImpl: GetFiltersOfTypeImpl): GetFiltersOfType

    @Binds
    fun bindUpdateFilter(updateFilterImpl: UpdateFilterImpl): UpdateFilter

    @Binds
    fun bindGetFilter(getFilterImpl: GetFilterImpl): GetFilter

    @Binds
    fun bindGetPermanentFilters(getPermanentFiltersImpl: GetPermanentFiltersImpl): GetPermanentFilters

    @Binds
    fun bindGetAllFiltertypes(impl: GetAllFilterTypesImpl): GetAllFilterTypes
}
