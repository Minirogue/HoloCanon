package com.minirogue.media.notes.internal.di

import com.minirogue.media.notes.UpdateCheckValue
import com.minirogue.media.notes.internal.usecase.GetNotesForMediaImpl
import com.minirogue.media.notes.internal.usecase.UpdateCheckValueImpl
import com.minirogue.starwarscanontracker.core.usecase.GetNotesForMedia
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface MediaNotesModule {
    @Binds
    fun bindUpdatecheckValue(impl: UpdateCheckValueImpl): UpdateCheckValue

    @Binds
    fun bindGetNotesForMedia(impl: GetNotesForMediaImpl): GetNotesForMedia
}
