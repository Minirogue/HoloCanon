package com.holocanon.library.sorting.internal.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.holocanon.library.sorting.internal.data.SortStyleRepository
import com.holocanon.library.sorting.usecase.GetSortStyle
import com.holocanon.library.sorting.usecase.ReverseSort
import com.holocanon.library.sorting.usecase.SaveSortStyle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class Sorting

private const val SORT_STYLE_DATASTORE_NAME = "sort-style"

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = SORT_STYLE_DATASTORE_NAME,
)

@Module
@InstallIn(SingletonComponent::class)
internal interface SortingModule {
    @Binds
    fun bindGetSortStyle(impl: SortStyleRepository): GetSortStyle

    @Binds
    fun bindSaveSortStyle(impl: SortStyleRepository): SaveSortStyle

    @Binds
    fun reverseSort(impl: SortStyleRepository): ReverseSort

    companion object {
        @Singleton
        @Sorting
        @Provides
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.datastore
    }
}
