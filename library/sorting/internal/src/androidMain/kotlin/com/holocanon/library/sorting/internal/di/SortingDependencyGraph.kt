package com.holocanon.library.sorting.internal.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.holocanon.library.sorting.internal.data.SortingDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

private const val SORT_STYLE_DATASTORE_NAME = "sort-style"

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = SORT_STYLE_DATASTORE_NAME,
)

@ContributesTo(AppScope::class)
interface SortingDependencyGraph {
    @Provides
    fun provideDataStore(application: Application): SortingDataStore =
        SortingDataStore(application.datastore)
}
