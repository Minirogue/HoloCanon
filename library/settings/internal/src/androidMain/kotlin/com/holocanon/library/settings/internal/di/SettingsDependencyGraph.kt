package com.holocanon.library.settings.internal.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import com.holocanon.library.settings.internal.data.SettingsDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

internal const val SETTINGS_DATASTORE_NAME = "settings"

private val Application.datastore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration({
                PreferenceManager.getDefaultSharedPreferences(
                    context,
                )
            }),
        )
    },
)

@ContributesTo(AppScope::class)
interface SettingsDependencyGraph {
    @SingleIn(AppScope::class)
    @Provides
    fun provideSettingsDatastore(application: Application): SettingsDataStore =
        SettingsDataStore(application.datastore)
}
