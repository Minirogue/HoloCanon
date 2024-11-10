package com.minirogue.series.di

import com.minirogue.series.usecase.GetSeries
import com.minirogue.series.usecase.GetSeriesIdFromName
import com.minirogue.series.usecase.GetSeriesIdFromNameImpl
import com.minirogue.series.usecase.GetSeriesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SeriesModule {
    @Binds
    fun bindGetSeries(impl: GetSeriesImpl): GetSeries

    @Binds
    fun bindGetSeriesIdFromName(impl: GetSeriesIdFromNameImpl): GetSeriesIdFromName
}
