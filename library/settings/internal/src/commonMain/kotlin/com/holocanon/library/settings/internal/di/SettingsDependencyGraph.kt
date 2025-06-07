package com.holocanon.library.settings.internal.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.holocanon.library.settings.internal.data.SettingsDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okio.Path.Companion.toPath

const val SETTINGS_DATASTORE_FILE_NAME = "datastore/settings.preferences_pb"

@ContributesTo(AppScope::class)
interface SettingsDependencyGraph {
    @SingleIn(AppScope::class)
    @Provides
    fun provideSettingsDatastore(
        settingsDataStorePath: SettingsDataStorePath,
    ): SettingsDataStore {
        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { settingsDataStorePath.pathAsString.toPath() },
        )
        return SettingsDataStore(datastore)
    }
}
