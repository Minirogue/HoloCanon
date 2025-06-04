package com.holocanon.library.settings.internal.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsAndroidDependencyGraph {
    @Provides
    fun provideDataStorePath(application: Application): SettingsDataStorePath {
        return SettingsDataStorePath(
            application
                .filesDir
                .resolve(SETTINGS_DATASTORE_FILE_NAME)
                .absolutePath,
        )
    }
}
