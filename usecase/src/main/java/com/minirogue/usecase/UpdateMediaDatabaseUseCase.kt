package com.minirogue.usecase

import android.app.Application
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.util.Log
import com.minirogue.api.media.Company
import com.minirogue.api.media.MediaType
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.holoclient.GetApiMediaVersion
import com.minirogue.holoclient.GetMediaFromApi
import com.minirogue.starwarscanontracker.core.R
import com.minirogue.starwarscanontracker.core.model.FilterUpdater
import com.minirogue.starwarscanontracker.core.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.core.model.room.entity.*
import com.minirogue.starwarscanontracker.core.result.HoloResult
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val TAG = "UpdateMediaDatabase"
public class UpdateMediaDatabaseUseCase @Inject constructor(
    private val application: Application,
    private val filterUpdater: FilterUpdater,
    private val connectivityManager: ConnectivityManager,
    private val getApiMediaVersion: GetApiMediaVersion,
    private val getMediaFromApi: GetMediaFromApi,
    private val database: MediaDatabase,
) {
    suspend operator fun invoke(forced: Boolean = false) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val wifiOnly =
            prefs.getBoolean(application.getString(R.string.setting_unmetered_sync_only), true)
        if (wifiOnly && connectivityManager.isActiveNetworkMetered) {
            return
        }
        val latestVersion = (getApiMediaVersion() as? HoloResult.Success)?.value

        if (!forced && latestVersion?.toLong() == PreferenceManager.getDefaultSharedPreferences(
                application
            )
                .getLong(application.getString(R.string.current_database_version), 0)
        ) {
            return
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
                .map {media ->
                    val series = media.series
                    if (seriesMap[series] == null && !series.isNullOrEmpty()) {
                        val seriesId = daoSeries.insert(Series().apply {title = series}).toInt()
                        seriesMap[series] = seriesId
                    }
                    media.toDTO(seriesMap, typeMap, companyMap)
                }
                .collect {
                    val insertSucceeded = daoMedia.insert(it)
                    if (insertSucceeded == -1L) daoMedia.update(it)
                    daoMedia.insert(MediaNotes(mediaId = it.id))
                }
        }

        if (latestVersion != null) {
            prefs.edit().apply {
                putLong(
                    application.getString(R.string.current_database_version),
                    latestVersion.toLong()
                )
                apply()
            }
        }
        filterUpdater.updateFilters()
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

    private suspend fun getCompanyMap(): Map<Company, Int> = try {
        val dtoCompanies = database.daoCompany.getAllCompanies()
        Company.values().associateWith { company ->
            val text = Json.encodeToString(company).trimQuotes()
            val dtoCompany = dtoCompanies.firstOrNull { it.companyName == text }

            dtoCompany?.id ?: database.daoCompany.insert(CompanyDto(companyName = text)).toInt()
        }
    } catch (e: Exception) {
        Log.e(TAG, "error getting company map: $e")
        emptyMap()
    }

    private suspend fun getTypeMap(): Map<MediaType, Int> = try {
        val dtoTypes = database.daoType.getAllMediaTypes()
        MediaType.values().associateWith { mediaType ->
            val text = Json.encodeToString(mediaType).trimQuotes()
            val dtoType = dtoTypes.firstOrNull { it.text == text }

            dtoType?.id ?: database.daoType.insert(MediaTypeDto().apply { setText(text) }).toInt()
        }
    } catch (e: Exception) {
        Log.e(TAG, "error getting type map: $e")
        emptyMap()
    }

    private fun String.trimQuotes() = this.trim('\"')
}
