package com.minirogue.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.minirogue.api.media.Company
import com.minirogue.api.media.MediaType
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.holoclient.GetApiMediaVersion
import com.minirogue.holoclient.GetMediaFromApi
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.entity.CompanyDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.Series
import com.minirogue.starwarscanontracker.core.result.HoloResult
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import settings.usecase.GetAllSettings
import settings.usecase.SetLatestDatabaseVersion
import javax.inject.Inject

private const val TAG = "UpdateMediaDatabase"

@Suppress("LongParameterList")
public class UpdateMediaDatabaseUseCase @Inject constructor(
        private val filterUpdater: FilterUpdater,
        private val connectivityManager: ConnectivityManager,
        private val getApiMediaVersion: GetApiMediaVersion,
        private val getMediaFromApi: GetMediaFromApi,
        private val database: MediaDatabase,
        private val getSettings: GetAllSettings,
        private val setLatestDatabaseVersion: SetLatestDatabaseVersion,
        private val context: Context,
) {
    operator fun invoke(forced: Boolean = false) = usecaseScope.launch {
        mutex.withLock {
            val settings = getSettings().first()
            val wifiOnly = settings.syncWifiOnly
            val latestLocalVersion = settings.latestDatabaseVersion
            if (wifiOnly && connectivityManager.isActiveNetworkMetered) {
                return@withLock
            }
            val latestRemoteVersion = (getApiMediaVersion() as? HoloResult.Success)?.value?.toLong()

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
                val daoMedia = database.daoMedia
                val daoSeries = database.daoSeries
                mediaList.asFlow()
                        .map { media ->
                            val series = media.series
                            if (seriesMap[series] == null && !series.isNullOrEmpty()) {
                                val seriesId = daoSeries.insert(Series().apply { title = series }).toInt()
                                seriesMap[series] = seriesId
                            }
                            media.toDTO(seriesMap, typeMap, companyMap)
                        }
                        .collect {
                            val insertSucceeded = daoMedia.insert(it)
                            if (insertSucceeded == -1L) daoMedia.update(it)
                            daoMedia.insert(MediaNotes(mediaId = it.id))
                        }
                if (latestRemoteVersion != null) {
                    setLatestDatabaseVersion(latestRemoteVersion)
                }
                filterUpdater.updateFilters()
            }
            withContext(Dispatchers.Main) { Toast.makeText(context, "Database Synced", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun StarWarsMedia.toDTO(
            seriesMap: Map<String, Int>,
            typeMap: Map<MediaType, Int>,
            companyMap: Map<Company, Int>
    ): MediaItem = MediaItem(
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
            database.daoSeries.getAllSeries().associate { it.title to it.id }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun getCompanyMap(): Map<Company, Int> = try {
        val dtoCompanies = database.daoCompany.getAllCompanies()
        Company.entries.associateWith { company ->
            val text = Json.encodeToString(company).trimQuotes()
            val dtoCompany = dtoCompanies.firstOrNull { it.companyName == text }

            dtoCompany?.id ?: database.daoCompany.insert(CompanyDto(companyName = text)).toInt()
        }
    } catch (e: Exception) {
        Log.e(TAG, "error getting company map: $e")
        emptyMap()
    }

    private fun getTypeMap(): Map<MediaType, Int> = MediaType.entries.associateWith { it.legacyId }

    private fun String.trimQuotes() = this.trim('\"')

    companion object {
        private val mutex = Mutex()
        private val usecaseScope = CoroutineScope(Job() + Dispatchers.IO)
    }
}
