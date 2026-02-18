package com.holocanon.library.sorting.internal.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SortingAndroidDependencyGraph {
    @Provides
    fun provideSortStyleDataStorePath(application: Application): SortStyleDataStorePath {
        return SortStyleDataStorePath(
            application
                .filesDir
                .resolve(SORT_STYLE_DATASTORE_FILE_NAME)
                .absolutePath,
        )
    }
}
