package com.minirogue.holocanon.feature.series.internal.di

import com.minirogue.holocanon.feature.series.GetSeriesFragment
import com.minirogue.holocanon.feature.series.internal.usecase.GetSeriesFragmentImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SeriesFeatureModule {
    @Binds
    fun bindGetSeriesFragment(impl: GetSeriesFragmentImpl): GetSeriesFragment
}