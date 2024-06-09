package com.holocanon.library.coroutine.ext.internal.di

import com.holocanon.library.coroutine.ext.ApplicationScope
import com.holocanon.library.coroutine.ext.internal.ApplicationScopeImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineExtModule {
    @Provides
    @Singleton
    fun provideAppScope(): ApplicationScope = ApplicationScopeImpl()
}