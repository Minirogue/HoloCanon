package com.minirogue.holocanon.feature.media.item.internal.di

import com.minirogue.holocanon.feature.media.item.internal.usecase.GetMediaItemFragmentImpl
import com.minirogue.holocanon.feature.media.item.usecase.GetMediaItemFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MediaItemFeatureModule {
    @Binds
    fun bindGetMediaItemFragment(impl: GetMediaItemFragmentImpl): GetMediaItemFragment
}
