package com.holocanon.library.media.item.internal.di

import com.holocanon.library.media.item.internal.usecase.GetMediaImpl
import com.holocanon.library.media.item.usecase.GetMedia
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MediaItemModule {
    @Binds
    fun bindGetMedia(impl: GetMediaImpl): GetMedia
}