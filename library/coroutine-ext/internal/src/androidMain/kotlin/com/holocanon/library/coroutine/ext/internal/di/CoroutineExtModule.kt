package com.holocanon.library.coroutine.ext.internal.di

import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface CoroutineExtModule {
    @Binds
    fun bindDispatchers(impl: HolocanonDispatchersImpl): HolocanonDispatchers
}
