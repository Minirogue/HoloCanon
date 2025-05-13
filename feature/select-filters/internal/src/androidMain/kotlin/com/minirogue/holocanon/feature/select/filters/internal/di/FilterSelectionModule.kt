package com.minirogue.holocanon.feature.select.filters.internal.di

import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.select.filters.internal.nav.FilterSelectionNavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
interface FilterSelectionModule {
    @Binds
    @IntoSet
    fun bindFilterSelectionNavContributor(impl: FilterSelectionNavContributor): NavContributor
}
