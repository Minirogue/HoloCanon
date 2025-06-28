package com.holocanon.library.sorting.internal.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.holocanon.library.sorting.internal.data.SortingDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path.Companion.toPath

const val SORT_STYLE_DATASTORE_FILE_NAME = "datastore/sort-style.preferences_pb"

@ContributesTo(AppScope::class)
interface SortingDependencyGraph {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDataStore(
        sortStyleDataStorePath: SortStyleDataStorePath,
    ): SortingDataStore {
        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { sortStyleDataStorePath.pathAsString.toPath() },
        )
        return SortingDataStore(datastore)
    }
}
