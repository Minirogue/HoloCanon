package settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.di.Settings
import settings.model.AllSettings
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import javax.inject.Inject

private const val userFilter1ActiveKey = "user_filter_1_active"
private const val userFilter2ActiveKey = "user_filter_2_active"
private const val userFilter3ActiveKey = "user_filter_3_active"
private const val syncWifiOnlyKey = "sync_wifi_only"
private const val checkbox1DefaultTextKey = "Completed"
private const val checkbox2DefaultTextKey = "Wishlist"
private const val checkbox3DefaultTextKey = "Owned"

internal class SettingsRepo @Inject constructor(@Settings private val dataStore: DataStore<Preferences>) {
    private val userFilter1ActivePreferenceKey = booleanPreferencesKey(userFilter1ActiveKey)
    private val userFilter2ActivePreferenceKey = booleanPreferencesKey(userFilter2ActiveKey)
    private val userFilter3ActivePreferenceKey = booleanPreferencesKey(userFilter3ActiveKey)
    private val syncWifiOnlyPreferenceKey = booleanPreferencesKey(syncWifiOnlyKey)
    private val checkbox1DefaultTextPreferenceKey = stringPreferencesKey(checkbox1DefaultTextKey)
    private val checkbox2DefaultTextPreferenceKey = stringPreferencesKey(checkbox2DefaultTextKey)
    private val checkbox3DefaultTextPreferenceKey = stringPreferencesKey(checkbox3DefaultTextKey)

    fun getSettings(): Flow<AllSettings> = dataStore.data.map { prefs ->
        AllSettings(
            checkboxSettings = CheckboxSettings(
                checkbox1Setting = CheckboxSetting( // TODO change default to resource
                    name = prefs[checkbox1DefaultTextPreferenceKey],
                    isInUse = prefs[userFilter1ActivePreferenceKey] ?: true
                ),
                checkbox2Setting = CheckboxSetting(
                    name = prefs[checkbox2DefaultTextPreferenceKey],
                    isInUse = prefs[userFilter2ActivePreferenceKey] ?: true
                ),
                checkbox3Setting = CheckboxSetting(
                    name = prefs[checkbox3DefaultTextPreferenceKey],
                    isInUse = prefs[userFilter3ActivePreferenceKey] ?: true
                )
            ),
            syncWifiOnly = prefs[syncWifiOnlyPreferenceKey] ?: true
        )
    }
}