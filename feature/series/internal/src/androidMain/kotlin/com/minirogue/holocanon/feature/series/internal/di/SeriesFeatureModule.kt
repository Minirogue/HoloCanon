package com.minirogue.holocanon.feature.series.internal.di

import com.holocanon.library.navigation.NavContributor
import com.minirogue.holocanon.feature.series.internal.nav.SeriesNavContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
internal interface SeriesFeatureModule {
    @Binds
    @IntoSet
    fun bindSeriesNavContributor(contributor: SeriesNavContributor): NavContributor
}
