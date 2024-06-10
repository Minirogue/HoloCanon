package com.holocanon.library.coroutine.ext.internal.di

import com.holocanon.library.coroutine.ext.ApplicationScope
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.holocanon.library.coroutine.ext.internal.ApplicationScopeImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface CoroutineExtModule {
    @Binds
    fun bindDispatchers(impl: HolocanonDispatchersImpl): HolocanonDispatchers

    companion object {
        @Provides
        @Singleton
        fun provideAppScope(): ApplicationScope = ApplicationScopeImpl()
    }
}