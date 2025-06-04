package com.holocanon.library.sorting.internal.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class SortingDataStore(dataStore: DataStore<Preferences>) : DataStore<Preferences> by dataStore
