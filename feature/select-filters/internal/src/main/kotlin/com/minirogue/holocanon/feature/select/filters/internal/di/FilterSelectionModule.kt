package com.minirogue.holocanon.feature.select.filters.internal.di

import com.minirogue.holocanon.feature.select.filters.internal.usecase.GetSelectFiltersFragmentImpl
import com.minirogue.holocanon.feature.select.filters.usecase.GetSelectFiltersFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FilterSelectionModule {
    @Binds
    fun bindGetSelectFiltersFragment(impl: GetSelectFiltersFragmentImpl): GetSelectFiltersFragment
}
