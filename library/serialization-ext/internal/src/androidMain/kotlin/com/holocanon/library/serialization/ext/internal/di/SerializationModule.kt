package com.holocanon.library.serialization.ext.internal.di

import com.holocanon.library.serialization.ext.internal.holocanonJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {
    @Provides
    fun provideJson(): Json = holocanonJson
}