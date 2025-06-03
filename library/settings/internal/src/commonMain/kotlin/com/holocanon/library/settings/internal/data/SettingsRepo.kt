package com.holocanon.library.settings.internal.data

import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minirogue.common.model.MediaType
import dev.zacsweers.metro.Inject
import holocanon.library.settings.internal.generated.resources.Res
import holocanon.library.settings.internal.generated.resources.settings_checkbox1_default_text
import holocanon.library.settings.internal.generated.resources.settings_checkbox2_default_text
import holocanon.library.settings.internal.generated.resources.settings_checkbox3_default_text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString
import settings.model.AllSettings
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.model.DarkModeSetting
import settings.model.Theme
import java.io.IOException

private const val USER_FILTER_1_ACTIVE_KEY = "user_filter_1_active"
private const val USER_FILTER_2_ACTIVE_KEY = "user_filter_2_active"
private const val USER_FILTER_3_ACTIVE_KEY = "user_filter_3_active"
private const val SYNC_WIFI_ONLY = "sync_wifi_only"
private const val CHECKBOX_1_DEFAULT_TEXT_KEY = "Completed"
private const val CHECKBOX_2_DEFAULT_TEXT_KEY = "Wishlist"
private const val CHECKBOX_3_DEFAULT_TEXT_KEY = "Owned"
private const val DATABASE_VERSION_KEY = "current database version"
private const val DARK_MODE_SETTING_KEY = "dark mode setting"
private const val THEME_SETTING_KEY = "theme setting"

private const val TAG = "SettingsRepo"

@Inject
class SettingsRepo(
    private val dataStore: SettingsDataStore,
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
    private val darkModeSettingPreferenceKey = stringPreferencesKey(DARK_MODE_SETTING_KEY)
    private val themeSettingPreferenceKey = stringPreferencesKey(THEME_SETTING_KEY)

    fun getSettings(): Flow<AllSettings> = dataStore.data.map { prefs ->
        AllSettings(
            checkboxSettings = CheckboxSettings(
                checkbox1Setting = CheckboxSetting(
                    name = prefs[checkbox1DefaultTextPreferenceKey]
                        ?: getDefaultNameForCheckbox(1),
                    isInUse = prefs[userFilter1ActivePreferenceKey] ?: true,
                ),
                checkbox2Setting = CheckboxSetting(
                    name = prefs[checkbox2DefaultTextPreferenceKey]
                        ?: getDefaultNameForCheckbox(2),
                    isInUse = prefs[userFilter2ActivePreferenceKey] ?: true,
                ),
                checkbox3Setting = CheckboxSetting(
                    name = prefs[checkbox3DefaultTextPreferenceKey]
                        ?: getDefaultNameForCheckbox(3),
                    isInUse = prefs[userFilter3ActivePreferenceKey] ?: true,
                ),
            ),
            syncWifiOnly = prefs[syncWifiOnlyPreferenceKey] ?: true,
            permanentFilterSettings = MediaType.entries.associateWith {
                prefs[booleanPreferencesKey(it.getSerialName())] ?: true
            },
            latestDatabaseVersion = prefs[databaseVersionPreferenceKey] ?: 0L,
            darkModeSetting = prefs[darkModeSettingPreferenceKey]?.let { prefValue ->
                DarkModeSetting.entries.find { it.name == prefValue }
            } ?: DarkModeSetting.SYSTEM,
            theme = prefs[themeSettingPreferenceKey]?.let { prefValue ->
                Theme.entries.find { it.name == prefValue }
            } ?: Theme.Force,
        )
    }

    /**
     * Updates the user-defined filter names and whether or not the checkboxes/filters are available.
     * Valid values for [whichBox] are 1, 2, or 3. If [newName] or [newUsageValue] are left null (default value),
     * then that value will not be updated.
     */
    suspend fun updateCheckbox(
        whichBox: Int,
        updateFunction: (CheckboxSetting) -> CheckboxSetting,
    ): Result<CheckboxSetting> {
        return runCatching {
            val nameKey = when (whichBox) {
                1 -> checkbox1DefaultTextPreferenceKey
                2 -> checkbox2DefaultTextPreferenceKey
                3 -> checkbox3DefaultTextPreferenceKey
                else -> error("updateCheckbox called with invalid whichBox: $whichBox")
            }
            val activeKey = when (whichBox) {
                1 -> userFilter1ActivePreferenceKey
                2 -> userFilter2ActivePreferenceKey
                3 -> userFilter3ActivePreferenceKey
                else -> error("updateCheckbox called with invalid whichBox: $whichBox")
            }
            val newPrefs = dataStore.edit { prefs ->
                val newCheckboxSetting = updateFunction(
                    CheckboxSetting(
                        name = prefs[nameKey] ?: getDefaultNameForCheckbox(whichBox),
                        isInUse = prefs[activeKey] ?: true,
                    ),
                )
                prefs[nameKey] = newCheckboxSetting.name
                prefs[activeKey] = newCheckboxSetting.isInUse
            }
            val newName = newPrefs[nameKey]
            val newIsInUse = newPrefs[activeKey]
            if (newName == null || newIsInUse == null) {
                error("couldn't get updated checkbox setting")
            } else {
                CheckboxSetting(newName, newIsInUse)
            }
        }.onFailure { throwable ->
            when (throwable) {
                is IOException -> Log.e(TAG, "error in updateCheckbox", throwable)
                is IllegalArgumentException -> Log.e(TAG, "error in updateCheckbox", throwable)
                is IllegalStateException -> Log.e(TAG, "error in updateCheckbox", throwable)
                else -> throw throwable
            }
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

    suspend fun updateDarkModeSetting(newValue: DarkModeSetting) {
        try {
            dataStore.edit { prefs ->
                prefs[darkModeSettingPreferenceKey] = newValue.name
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updateDarkModeSetting", e)
        }
    }

    suspend fun updateTheme(newValue: Theme) {
        try {
            dataStore.edit { prefs ->
                prefs[themeSettingPreferenceKey] = newValue.name
            }
        } catch (e: IOException) {
            Log.e(TAG, "error in updateTheme", e)
        }
    }

    private suspend fun getDefaultNameForCheckbox(whichBox: Int): String = when (whichBox) {
        1 -> getString(Res.string.settings_checkbox1_default_text)
        2 -> getString(Res.string.settings_checkbox2_default_text)
        3 -> getString(Res.string.settings_checkbox3_default_text)
        else -> error("attempting to get name for checkbox that isn't 1, 2, or 3")
    }
}
