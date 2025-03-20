package com.minirogue.media.notes.internal.di

import com.minirogue.media.notes.ExportMediaNotesJson
import com.minirogue.media.notes.ImportMediaNotesJson
import com.minirogue.media.notes.internal.usecase.ExportMediaNotesJsonImpl
import com.minirogue.media.notes.internal.usecase.GetNotesForMediaImpl
import com.minirogue.media.notes.internal.usecase.ImportMediaNotesJsonImpl
import com.minirogue.media.notes.internal.usecase.UpdateCheckValueImpl
import com.minirogue.media.notes.usecase.GetNotesForMedia
import com.minirogue.media.notes.usecase.UpdateCheckValue
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

    @Binds
    fun bindGetMediaNotesAsJson(impl: ExportMediaNotesJsonImpl): ExportMediaNotesJson

    @Binds
    fun bindImportMediaNotesJson(impl: ImportMediaNotesJsonImpl): ImportMediaNotesJson
}
