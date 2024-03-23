package com.minirogue.media.notes.di

import com.minirogue.media.notes.UpdateCheckValue
import com.minirogue.media.notes.internal.UpdateCheckValueImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MediaNotesModule {
    @Binds
    fun bindUpdatecheckValue(impl: UpdateCheckValueImpl): UpdateCheckValue
}
