package settings.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minirogue.api.media.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.di.Settings
import settings.model.AllSettings
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import java.io.IOException
import javax.inject.Inject

private const val userFilter1ActiveKey = "user_filter_1_active"
private const val userFilter2ActiveKey = "user_filter_2_active"
private const val userFilter3ActiveKey = "user_filter_3_active"
private const val syncWifiOnlyKey = "sync_wifi_only"
private const val checkbox1DefaultTextKey = "Completed"
private const val checkbox2DefaultTextKey = "Wishlist"
private const val checkbox3DefaultTextKey = "Owned"
private const val databaseVersionKey = "current database version"
private const val TAG = "SettingsRepo"

internal class SettingsRepo @Inject constructor(@Settings private val dataStore: DataStore<Preferences>) {
    private val userFilter1ActivePreferenceKey = booleanPreferencesKey(userFilter1ActiveKey)
    private val userFilter2ActivePreferenceKey = booleanPreferencesKey(userFilter2ActiveKey)
    private val userFilter3ActivePreferenceKey = booleanPreferencesKey(userFilter3ActiveKey)
    private val syncWifiOnlyPreferenceKey = booleanPreferencesKey(syncWifiOnlyKey)
    private val checkbox1DefaultTextPreferenceKey = stringPreferencesKey(checkbox1DefaultTextKey)
    private val checkbox2DefaultTextPreferenceKey = stringPreferencesKey(checkbox2DefaultTextKey)
    private val checkbox3DefaultTextPreferenceKey = stringPreferencesKey(checkbox3DefaultTextKey)
    private val databaseVersionPreferenceKey = longPreferencesKey(databaseVersionKey)

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
            syncWifiOnly = prefs[syncWifiOnlyPreferenceKey] ?: true,
            permanentFilterSettings = MediaType.values().associateWith { prefs[booleanPreferencesKey(it.getSerialname())] ?: true },
            latestDatabaseVersion = prefs[databaseVersionPreferenceKey] ?: 0L
        )
    }

    /**
     * Updates the user-defined filter names and whether or not the checkboxes/filters are available.
     * Valid values for [whichBox] are 1, 2, or 3. If [newName] or [newUsageValue] are left null (default value),
     * then that value will not be updated.
     */
    suspend fun updateCheckbox(whichBox: Int, newName: String? = null, newUsageValue: Boolean? = null) {
        try {
            val nameKey = when (whichBox) {
                1 -> checkbox1DefaultTextPreferenceKey
                2 -> checkbox2DefaultTextPreferenceKey
                3 -> checkbox3DefaultTextPreferenceKey
                else -> throw IllegalArgumentException("updateCheckbox called with invalid whichBox: $whichBox")
            }
            val activeKey = when (whichBox) {
                1 -> userFilter1ActivePreferenceKey
                2 -> userFilter2ActivePreferenceKey
                3 -> userFilter3ActivePreferenceKey
                else -> throw IllegalArgumentException("updateCheckbox called with invalid whichBox: $whichBox")
            }
            dataStore.edit { prefs ->
                newName?.also { prefs[nameKey] = it }
                newUsageValue?.also { prefs[activeKey] = it }
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updateCheckbox", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "error in updateCheckbox", e)
        }
    }

    suspend fun updatePermanentFilter(mediaType: MediaType, isActive: Boolean) {
        try {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(mediaType.getSerialname())] = isActive
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updatePermanentFilter", e)
        }
    }

    suspend fun updateDatabaseVersionNumber(newVersionNumber: Long) {
        try {
            dataStore.edit { prefs ->
                prefs[databaseVersionPreferenceKey] = newVersionNumber
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updateDatabaseVersionNumber", e)
        }
    }

    suspend fun updateWifiSetting(newvalue: Boolean) {
        try {
            dataStore.edit { prefs ->
                prefs[syncWifiOnlyPreferenceKey] = newvalue
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updateWifiSetting", e)
        }
    }
}