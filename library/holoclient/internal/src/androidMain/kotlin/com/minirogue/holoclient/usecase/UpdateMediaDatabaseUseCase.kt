package com.minirogue.holoclient.usecase

import android.app.Application
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.holocanon.core.data.entity.SeriesDto
import com.holocanon.library.filters.usecase.UpdateFilters
import com.minirogue.common.model.Company
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.holoclient.api.GetApiMediaVersion
import com.minirogue.holoclient.api.GetMediaFromApi
import com.minirogue.holoclient.api.HoloResult
import com.minirogue.starwarscanontracker.core.data.database.MediaDatabase
import com.minirogue.starwarscanontracker.core.data.entity.CompanyDto
import com.minirogue.starwarscanontracker.core.data.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.data.entity.MediaNotesDto
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import settings.usecase.GetAllSettings
import settings.usecase.SetLatestDatabaseVersion

@Inject
@ContributesBinding(AppScope::class)
class UpdateMediaDatabaseUseCase internal constructor(
    private val updateFilters: UpdateFilters,
    private val connectivityManager: ConnectivityManager,
    private val getApiMediaVersion: GetApiMediaVersion,
    private val getMediaFromApi: GetMediaFromApi,
    private val database: MediaDatabase,
    private val getSettings: GetAllSettings,
    private val setLatestDatabaseVersion: SetLatestDatabaseVersion,
    private val context: Application,
    private val json: Json,
) : MaybeUpdateMediaDatabase {
    override fun invoke(forced: Boolean) {
        usecaseScope.launch {
            mutex.withLock {
                val settings = getSettings().first()
                val wifiOnly = settings.syncWifiOnly
                val latestLocalVersion = settings.latestDatabaseVersion
                if (wifiOnly && connectivityManager.isActiveNetworkMetered) {
                    return@withLock
                }
                val latestRemoteVersion =
                    (getApiMediaVersion() as? HoloResult.Success)?.value?.toLong()

                if (!forced && latestRemoteVersion == latestLocalVersion) {
                    return@withLock
                }

                val seriesMap: MutableMap<String, Int> =
                    getSeriesMap().toMutableMap()
                val companyMap: Map<Company, Int> =
                    getCompanyMap()
                val typeMap: Map<MediaType, Int> =
                    getTypeMap()

                (getMediaFromApi() as? HoloResult.Success)?.value?.let { mediaList ->
                    val daoMedia = database.getDaoMedia()
                    val daoSeries = database.getDaoSeries()
                    mediaList.asFlow()
                        .map { media ->
                            val series = media.series
                            if (seriesMap[series] == null && !series.isNullOrEmpty()) {
                                val seriesDtoId =
                                    daoSeries.insert(
                                        com.holocanon.core.data.entity.SeriesDto()
                                            .apply { title = series },
                                    ).toInt()
                                seriesMap[series] = seriesDtoId
                            }
                            media.toDTO(seriesMap, typeMap, companyMap)
                        }
                        .collect {
                            val insertSucceeded = daoMedia.insert(it)
                            if (insertSucceeded == -1L) daoMedia.update(it)
                            daoMedia.insert(MediaNotesDto(mediaId = it.id))
                        }
                    if (latestRemoteVersion != null) {
                        setLatestDatabaseVersion(latestRemoteVersion)
                    }
                    updateFilters()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Database Synced",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    private fun StarWarsMedia.toDTO(
        seriesMap: Map<String, Int>,
        typeMap: Map<MediaType, Int>,
        companyMap: Map<Company, Int>,
    ): MediaItemDto = MediaItemDto(
        // TODO add "number" and remove unused fields
        id = this.id.toInt(),
        title = this.title,
        series = this.series?.let { seriesMap[it] } ?: -1,
        author = "",
        type = typeMap[this.type] ?: -1,
        description = this.description ?: "",
        review = "",
        imageURL = this.imageUrl ?: "",
        date = this.releaseDate,
        timeline = this.timeline?.toDouble() ?: 10000.0,
        amazonLink = "",
        amazonStream = "",
        publisher = companyMap[this.publisher] ?: -1,
    )

    private suspend fun getSeriesMap() =
        database.getDaoSeries().getAllSeries().first().associate { it.title to it.id }

    private suspend fun getCompanyMap(): Map<Company, Int> = try {
        val dtoCompanies = database.getDaoCompany().getAllCompanies().first()
        Company.entries.associateWith { company ->
            val text = json.encodeToString(company).trimQuotes()
            val dtoCompany = dtoCompanies.firstOrNull { it.companyName == text }

            dtoCompany?.id ?: database.getDaoCompany().insert(CompanyDto(companyName = text))
                .toInt()
        }
    } catch (e: Exception) {
        Log.e(TAG, "error getting company map: $e")
        emptyMap()
    }

    private fun getTypeMap(): Map<MediaType, Int> = MediaType.entries.associateWith { it.legacyId }

    private fun String.trimQuotes() = this.trim('\"')

    companion object {
        private const val TAG = "UpdateMediaDatabase"

        private val usecaseScope = CoroutineScope(Job() + Dispatchers.IO)
        private val mutex = Mutex()
    }
}
