package com.holocanon.library.settings.internal.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class SettingsDataStore(dataStore: DataStore<Preferences>) : DataStore<Preferences> by dataStore
