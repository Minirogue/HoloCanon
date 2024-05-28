package settings.data

import android.content.res.Resources
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minirogue.common.model.MediaType
import com.minirogue.holocanon.library.settings.internal.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.di.Settings
import settings.model.AllSettings
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import java.io.IOException
import javax.inject.Inject

private const val USER_FILTER_1_ACTIVE_KEY = "user_filter_1_active"
private const val USER_FILTER_2_ACTIVE_KEY = "user_filter_2_active"
private const val USER_FILTER_3_ACTIVE_KEY = "user_filter_3_active"
private const val SYNC_WIFI_ONLY = "sync_wifi_only"
private const val CHECKBOX_1_DEFAULT_TEXT_KEY = "Completed"
private const val CHECKBOX_2_DEFAULT_TEXT_KEY = "Wishlist"
private const val CHECKBOX_3_DEFAULT_TEXT_KEY = "Owned"
private const val DATABASE_VERSION_KEY = "current database version"
private const val TAG = "SettingsRepo"

internal class SettingsRepo @Inject constructor(
    @Settings private val dataStore: DataStore<Preferences>,
    private val resources: Resources,
) {
    private val userFilter1ActivePreferenceKey = booleanPreferencesKey(USER_FILTER_1_ACTIVE_KEY)
    private val userFilter2ActivePreferenceKey = booleanPreferencesKey(USER_FILTER_2_ACTIVE_KEY)
    private val userFilter3ActivePreferenceKey = booleanPreferencesKey(USER_FILTER_3_ACTIVE_KEY)
    private val syncWifiOnlyPreferenceKey = booleanPreferencesKey(SYNC_WIFI_ONLY)
    private val checkbox1DefaultTextPreferenceKey =
        stringPreferencesKey(CHECKBOX_1_DEFAULT_TEXT_KEY)
    private val checkbox2DefaultTextPreferenceKey =
        stringPreferencesKey(CHECKBOX_2_DEFAULT_TEXT_KEY)
    private val checkbox3DefaultTextPreferenceKey =
        stringPreferencesKey(CHECKBOX_3_DEFAULT_TEXT_KEY)
    private val databaseVersionPreferenceKey = longPreferencesKey(DATABASE_VERSION_KEY)

    fun getSettings(): Flow<AllSettings> = dataStore.data.map { prefs ->
        AllSettings(
            checkboxSettings = CheckboxSettings(
                checkbox1Setting = CheckboxSetting(
                    name = prefs[checkbox1DefaultTextPreferenceKey]
                        ?: resources.getString(R.string.settings_checkbox1_default_text),
                    isInUse = prefs[userFilter1ActivePreferenceKey] ?: true
                ),
                checkbox2Setting = CheckboxSetting(
                    name = prefs[checkbox2DefaultTextPreferenceKey]
                        ?: resources.getString(R.string.settings_checkbox2_default_text),
                    isInUse = prefs[userFilter2ActivePreferenceKey] ?: true
                ),
                checkbox3Setting = CheckboxSetting(
                    name = prefs[checkbox3DefaultTextPreferenceKey]
                        ?: resources.getString(R.string.settings_checkbox3_default_text),
                    isInUse = prefs[userFilter3ActivePreferenceKey] ?: true
                )
            ),
            syncWifiOnly = prefs[syncWifiOnlyPreferenceKey] ?: true,
            permanentFilterSettings = MediaType.entries.associateWith {
                prefs[booleanPreferencesKey(it.getSerialName())] ?: true
            },
            latestDatabaseVersion = prefs[databaseVersionPreferenceKey] ?: 0L
        )
    }

    /**
     * Updates the user-defined filter names and whether or not the checkboxes/filters are available.
     * Valid values for [whichBox] are 1, 2, or 3. If [newName] or [newUsageValue] are left null (default value),
     * then that value will not be updated.
     */
    suspend fun updateCheckbox(
        whichBox: Int,
        newName: String? = null,
        newUsageValue: Boolean? = null
    ) {
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
                prefs[booleanPreferencesKey(mediaType.getSerialName())] = isActive
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
