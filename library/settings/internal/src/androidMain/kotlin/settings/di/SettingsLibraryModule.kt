package settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import settings.usecase.FlipIsCheckboxActive
import settings.usecase.FlipIsCheckboxActiveImpl
import settings.usecase.GetAllSettings
import settings.usecase.GetAllSettingsImpl
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxSettingsImpl
import settings.usecase.GetPermanentFilterSettings
import settings.usecase.GetPermanentFilterSettingsImpl
import settings.usecase.SetLatestDatabaseVersion
import settings.usecase.SetLatestDatabaseVersionImpl
import settings.usecase.ShouldSyncViaWifiOnly
import settings.usecase.ShouldSyncViaWifiOnlyImpl
import settings.usecase.UpdateCheckboxName
import settings.usecase.UpdateCheckboxNameImpl
import settings.usecase.UpdatePermanentFilterSettings
import settings.usecase.UpdatePermanentFilterSettingsImpl
import settings.usecase.UpdateTheme
import settings.usecase.UpdateThemeImpl
import settings.usecase.UpdateWifiSetting
import settings.usecase.UpdateWifiSettingImpl
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class Settings

private const val SETTINGS_DATASTORE_NAME = "settings"

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
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

@Module
@InstallIn(SingletonComponent::class)
internal interface SettingsLibraryModule {
    @Binds
    fun bindGetCheckboxSettings(getCheckboxSettingsImpl: GetCheckboxSettingsImpl): GetCheckboxSettings

    @Binds
    fun bindGetAllSettings(getAllSettingsImpl: GetAllSettingsImpl): GetAllSettings

    @Binds
    fun bindShouldSyncViaWifiOnly(shouldSyncViaWifiOnlyImpl: ShouldSyncViaWifiOnlyImpl): ShouldSyncViaWifiOnly

    @Binds
    fun bindUpdateCheckboxActive(updateCheckboxActiveImpl: FlipIsCheckboxActiveImpl): FlipIsCheckboxActive

    @Binds
    fun bindUpdateCheckboxName(updateCheckboxNameImpl: UpdateCheckboxNameImpl): UpdateCheckboxName

    @Binds
    fun bindGetPermanentFilterSettings(
        getPermanentFilterSettingsImpl: GetPermanentFilterSettingsImpl,
    ): GetPermanentFilterSettings

    @Binds
    fun bindUpdatePermanentFilterSettings(
        updatePermanentFilterSettingsImpl: UpdatePermanentFilterSettingsImpl,
    ): UpdatePermanentFilterSettings

    @Binds
    fun bindUpdateWifiSetting(updateWifiSettingImpl: UpdateWifiSettingImpl): UpdateWifiSetting

    @Binds
    fun bindSetDatabaseVersion(impl: SetLatestDatabaseVersionImpl): SetLatestDatabaseVersion

    @Binds
    fun bindUpdateTheme(impl: UpdateThemeImpl): UpdateTheme

    companion object {
        @Singleton
        @Settings
        @Provides
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.datastore
    }
}
