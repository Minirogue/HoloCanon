package com.holocanon.app.shared.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.holocanon.feature.global.notification.internal.usecase.GetGlobalToastsImpl
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import com.holocanon.library.filters.internal.UpdateFiltersImpl
import com.holocanon.library.filters.usecase.UpdateFilters
import com.holocanon.library.holoclient.internal.usecase.UpdateMediaDatabaseUseCase
import com.holocanon.library.settings.internal.usecase.GetAllSettingsImpl
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import settings.usecase.GetAllSettings
import com.holocanon.library.filters.internal.GetFilterImpl
import com.holocanon.library.filters.internal.UpdateFilterImpl
import com.holocanon.library.filters.internal.GetActiveFiltersImpl
import com.holocanon.library.filters.internal.GetAllFilterGroupsImpl
import com.holocanon.library.logger.internal.HoloLoggerImpl
import com.holocanon.library.sorting.internal.data.SortStyleRepository
import com.holocanon.library.coroutine.ext.internal.di.HolocanonDispatchersImpl
import com.holocanon.library.media.notes.internal.usecase.GetNotesForMediaImpl
import com.holocanon.library.settings.internal.usecase.UpdatePermanentFilterSettingsImpl
import com.holocanon.library.settings.internal.usecase.UpdateDarkModeSettingImpl
import com.holocanon.library.settings.internal.usecase.GetPermanentFilterSettingsImpl
import com.holocanon.library.settings.internal.usecase.ShouldSyncViaWifiOnlyImpl
import com.holocanon.library.settings.internal.usecase.UpdateWifiSettingImpl
import com.holocanon.library.settings.internal.usecase.SetLatestDatabaseVersionImpl
import com.holocanon.library.media.notes.internal.usecase.UpdateCheckValueImpl
import com.holocanon.library.media.item.internal.usecase.GetMediaImpl
import com.holocanon.library.settings.internal.usecase.UpdateThemeImpl
import filters.GetFilter
import filters.UpdateFilter
import filters.GetActiveFilters
import filters.GetAllFilterGroups
import com.holocanon.library.logger.HoloLogger
import com.holocanon.library.sorting.usecase.GetSortStyle
import com.holocanon.library.sorting.usecase.SaveSortStyle
import com.holocanon.library.sorting.usecase.ReverseSort
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.minirogue.media.notes.usecase.GetNotesForMedia
import settings.usecase.UpdatePermanentFilterSettings
import settings.usecase.UpdateDarkModeSetting
import settings.usecase.GetPermanentFilterSettings
import settings.usecase.ShouldSyncViaWifiOnly
import settings.usecase.UpdateWifiSetting
import settings.usecase.SetLatestDatabaseVersion
import com.minirogue.media.notes.usecase.UpdateCheckValue
import com.holocanon.library.media.item.usecase.GetMedia
import settings.usecase.UpdateTheme
import com.holocanon.core.data.dao.DaoCompany
import com.holocanon.core.data.dao.DaoFilter
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.data.dao.DaoSeries
import com.holocanon.core.data.database.MediaDatabase
import com.holocanon.core.data.database.MediaDatabase.Companion.DATABASE_NAME
import com.holocanon.feature.global.notification.internal.usecase.SendGlobalToastimpl
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import com.holocanon.feature.settings.internal.nav.SettingsNavContributor
import com.holocanon.library.filters.internal.GetPermanentFiltersImpl
import com.holocanon.library.logger.internal.IosLoggerDelegate
import com.holocanon.library.logger.internal.LoggerDelegate
import com.holocanon.library.media.item.internal.usecase.GetMediaAndNotesForSeriesImpl
import com.holocanon.library.media.item.internal.usecase.GetMediaListWithNotesImpl
import com.holocanon.library.media.item.usecase.GetMediaAndNotesForSeries
import com.holocanon.library.media.item.usecase.GetMediaListWithNotes
import com.holocanon.library.media.notes.internal.usecase.ExportMediaNotesJsonImpl
import com.holocanon.library.media.notes.internal.usecase.ImportMediaNotesJsonImpl
import com.holocanon.library.navigation.NavContributor
import com.holocanon.library.networking.HttpClientWrapper
import com.holocanon.library.networking.internal.HttpClientWrapperImpl
import com.holocanon.library.platform.Platform
import com.holocanon.library.serialization.ext.internal.HolocanonJson
import com.holocanon.library.settings.internal.data.SettingsDataStore
import com.holocanon.library.settings.internal.di.SETTINGS_DATASTORE_FILE_NAME
import com.holocanon.library.settings.internal.di.SettingsDataStorePath
import com.holocanon.library.settings.internal.usecase.FlipIsCheckboxActiveImpl
import com.holocanon.library.settings.internal.usecase.GetCheckboxSettingsImpl
import com.holocanon.library.settings.internal.usecase.IsNetworkAllowedImpl
import com.holocanon.library.settings.internal.usecase.UpdateCheckboxNameImpl
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import com.holocanon.library.sorting.internal.data.SortingDataStore
import com.holocanon.library.sorting.internal.di.SORT_STYLE_DATASTORE_FILE_NAME
import com.holocanon.library.sorting.internal.di.SortStyleDataStorePath
import com.minirogue.holocanon.feature.media.item.internal.nav.MediaItemNavContributor
import com.minirogue.holocanon.feature.media.list.internal.nav.MediaListNavContributor
import com.minirogue.holocanon.feature.select.filters.internal.nav.FilterSelectionNavContributor
import com.minirogue.holocanon.feature.series.internal.nav.SeriesNavContributor
import com.minirogue.media.notes.ExportMediaNotesJson
import com.minirogue.media.notes.ImportMediaNotesJson
import com.minirogue.series.usecase.GetSeries
import com.minirogue.series.usecase.GetSeriesIdFromName
import com.minirogue.series.usecase.GetSeriesIdFromNameImpl
import com.minirogue.series.usecase.GetSeriesImpl
import com.minirogue.series.usecase.SetCheckboxForSeries
import com.minirogue.series.usecase.SetCheckboxForSeriesImpl
import dev.zacsweers.metro.SingleIn
import filters.GetPermanentFilters
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import screen.nav.HomeNavContributor
import settings.usecase.FlipIsCheckboxActive
import settings.usecase.GetCheckboxSettings
import settings.usecase.UpdateCheckboxName

@DependencyGraph(AppScope::class)
interface IosDependencyGraph : AppDependencyGraph {
    @Binds
    fun bindGetGlobalToasts(impl: GetGlobalToastsImpl): GetGlobalToasts

    @Binds
    fun bindGetAllSettings(impl: GetAllSettingsImpl): GetAllSettings

    @Binds
    fun bindUpdateFilters(impl: UpdateFiltersImpl): UpdateFilters

    @Binds
    fun bindMaybeUpdateMediaDatabase(impl: UpdateMediaDatabaseUseCase): MaybeUpdateMediaDatabase

    @Binds
    fun bindGetFilter(impl: GetFilterImpl): GetFilter

    @Binds
    fun bindUpdateFilter(impl: UpdateFilterImpl): UpdateFilter

    @Binds
    fun bindGetActiveFilters(impl: GetActiveFiltersImpl): GetActiveFilters

    @Binds
    fun bindGetAllFilterGroups(impl: GetAllFilterGroupsImpl): GetAllFilterGroups

    @Binds
    fun bindHoloLogger(impl: HoloLoggerImpl): HoloLogger

    @Binds
    fun bindGetSortStyle(impl: SortStyleRepository): GetSortStyle

    @Binds
    fun bindSaveSortStyle(impl: SortStyleRepository): SaveSortStyle

    @Binds
    fun bindReverseSort(impl: SortStyleRepository): ReverseSort

    @Binds
    fun bindHolocanonDispatchers(impl: HolocanonDispatchersImpl): HolocanonDispatchers

    @Binds
    fun bindGetNotesForMedia(impl: GetNotesForMediaImpl): GetNotesForMedia

    @Binds
    fun bindUpdatePermanentFilterSettings(impl: UpdatePermanentFilterSettingsImpl): UpdatePermanentFilterSettings

    @Binds
    fun bindUpdateDarkModeSetting(impl: UpdateDarkModeSettingImpl): UpdateDarkModeSetting

    @Binds
    fun bindGetPermanentFilterSettings(impl: GetPermanentFilterSettingsImpl): GetPermanentFilterSettings

    @Binds
    fun bindShouldSyncViaWifiOnly(impl: ShouldSyncViaWifiOnlyImpl): ShouldSyncViaWifiOnly

    @Binds
    fun bindUpdateWifiSetting(impl: UpdateWifiSettingImpl): UpdateWifiSetting

    @Binds
    fun bindSetLatestDatabaseVersion(impl: SetLatestDatabaseVersionImpl): SetLatestDatabaseVersion

    @Binds
    fun bindUpdateCheckValue(impl: UpdateCheckValueImpl): UpdateCheckValue

    @Binds
    fun bindGetMedia(impl: GetMediaImpl): GetMedia

    @Binds
    fun bindUpdateTheme(impl: UpdateThemeImpl): UpdateTheme

    @Binds
    fun bindGetCheckboxSettings(impl: GetCheckboxSettingsImpl): GetCheckboxSettings

    @Binds
    fun bindSendGlobalToast(impl: SendGlobalToastimpl): SendGlobalToast

    @Binds
    fun bindIsNetworkAvailable(impl: IsNetworkAllowedImpl): IsNetworkAllowed

    @Binds
    fun bindGetPermanentFilters(impl: GetPermanentFiltersImpl): GetPermanentFilters

    @Binds
    fun bindHttpClientWrapper(impl: HttpClientWrapperImpl): HttpClientWrapper

    @Binds
    fun bindGetMediaListWithNotes(impl: GetMediaListWithNotesImpl): GetMediaListWithNotes

    @Binds
    fun bindUpdateCheckboxName(impl: UpdateCheckboxNameImpl): UpdateCheckboxName

    @Binds
    fun bindFlipCheckboxActive(impl: FlipIsCheckboxActiveImpl): FlipIsCheckboxActive

    @Binds
    fun bindExportMediaNotesJson(impl: ExportMediaNotesJsonImpl): ExportMediaNotesJson

    @Binds
    fun bindImportMediaNotesJson(impl: ImportMediaNotesJsonImpl): ImportMediaNotesJson

    @Binds
    fun bindGetSeries(impl: GetSeriesImpl): GetSeries

    @Binds
    fun bindGetMediaAndNotesForSeries(impl: GetMediaAndNotesForSeriesImpl): GetMediaAndNotesForSeries

    @Binds
    fun bindSetCheckboxForSeries(impl: SetCheckboxForSeriesImpl): SetCheckboxForSeries

    @Binds
    fun bindGetSeriesIdFromName(impl: GetSeriesIdFromNameImpl): GetSeriesIdFromName

    @Provides
    fun provideNavContributors(
        filterSelectionNavContributor: FilterSelectionNavContributor,
        homeNavContributor: HomeNavContributor,
        mediaListNavContributor: MediaListNavContributor,
        mediaItemNavContributor: MediaItemNavContributor,
        seriesNavContributor: SeriesNavContributor,
        settingsNavContributor: SettingsNavContributor,
    ): Set<NavContributor> = setOf(
        filterSelectionNavContributor,
        homeNavContributor,
        mediaItemNavContributor,
        mediaListNavContributor,
        seriesNavContributor,
        settingsNavContributor
    )

    @Provides
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    @Provides
    fun providePlatform(): Platform = Platform.IOS

    @Provides
    @SingleIn(AppScope::class)
    fun provideSortingDataStore(
        sortStyleDataStorePath: SortStyleDataStorePath,
    ): SortingDataStore {
        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { sortStyleDataStorePath.pathAsString.toPath() },
        )
        return SortingDataStore(datastore)
    }

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    fun provideSortStyleDataStorePath(): SortStyleDataStorePath {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return SortStyleDataStorePath(requireNotNull(documentDirectory).path + "/$SORT_STYLE_DATASTORE_FILE_NAME")
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideSettingsDataStore(
        settingsDataStorePath: SettingsDataStorePath,
    ): SettingsDataStore {
        val datastore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { settingsDataStorePath.pathAsString.toPath() },
        )
        return SettingsDataStore(datastore)
    }

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    fun provideSettingsDataStorePath(): SettingsDataStorePath {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return SettingsDataStorePath(requireNotNull(documentDirectory).path + "/${SETTINGS_DATASTORE_FILE_NAME}")
    }

    @Provides
    fun provideJson(): Json = HolocanonJson()

    @Provides
    fun provideDaoMedia(database: MediaDatabase): DaoMedia = database.getDaoMedia()

    @Provides
    fun provideDaoFilter(database: MediaDatabase): DaoFilter = database.getDaoFilter()

    @Provides
    fun provideDaoSeries(database: MediaDatabase): DaoSeries = database.getDaoSeries()

    @Provides
    fun provideDaoCompany(database: MediaDatabase): DaoCompany = database.getDaoCompany()

    @Provides
    fun provideIosLogger(): Set<LoggerDelegate> = setOf(IosLoggerDelegate())

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    @SingleIn(AppScope::class)
    fun providesMediaDatabase(): MediaDatabase {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )

        val dbFilePath = documentDirectory?.path?.let { "$it/$DATABASE_NAME.db" }
        return if (dbFilePath != null) {
            Room.databaseBuilder<MediaDatabase>(
                name = dbFilePath,
            ).setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        } else error("Document directory is null")
    }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides platformDependencies: PlatformDependencies): IosDependencyGraph
    }
}

actual fun getAppDependencyGraph(platformDependencies: PlatformDependencies): AppDependencyGraph =
    createGraphFactory<IosDependencyGraph.Factory>().create(platformDependencies)
