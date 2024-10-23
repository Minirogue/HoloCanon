package com.minirogue.holoclient.di

import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.holoclient.usecase.UpdateMediaDatabaseUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface HoloclientModule {
    @Binds
    fun bindMaybeUpdateMediaDatabase(impl: UpdateMediaDatabaseUseCase): MaybeUpdateMediaDatabase
}
