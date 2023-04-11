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
            getSeriesMap().toMutableMap().also { Log.d("holo-client", "seriesMap: $it") }
        val companyMap: Map<Company, Int> =
            getCompanyMap().also { Log.d("holo-client", "companyMap: $it") }
        val typeMap: Map<MediaType, Int> =
            getTypeMap().also { Log.d("holo-client", "typeMap: $it") }

        (getMediaFromApi() as? HoloResult.Success)?.value?.let { mediaList ->
            val daoMedia = database.daoMedia
            val daoSeries = database.daoSeries
            mediaList.asFlow()
                .map {media ->
                    val series = media.series
                    if (seriesMap[series] == null && !series.isNullOrEmpty()) {
                        Log.d("holo-client","series $series not found in map, adding to database")
                        val seriesId = daoSeries.insert(Series().apply {title = series}).toInt()
                        seriesMap[series] = seriesId.also { Log.d("holo-client", "$series added to map with id $it") }
                    }
                    media.toDTO(seriesMap, typeMap, companyMap)
                }
                .collect {
                    Log.d("holo-client", "inserting media $it")
                    val insertSucceeded = daoMedia.insert(it)
                    if (insertSucceeded == -1L) daoMedia.update(it).also {Log.d("holo-client", "insert failed, updating instead")}
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
        filterUpdater.updateFilters();
    }

    private fun StarWarsMedia.toDTO(
        seriesMap: Map<String, Int>,
        typeMap: Map<MediaType, Int>,
        companyMap: Map<Company, Int>
    ): MediaItem = MediaItem(
        id = this.id.toInt(),
        title = this.title,
        series = this.series?.let { seriesMap[it] } ?: -1.also {
            Log.d(
                "holo-client",
                "failed to map series ${this.series}"
            )
        },
        author = "",
        type = typeMap[this.type] ?: -1.also {
            Log.d(
                "holo-client",
                "failed to map type ${this.type}"
            )
        },
        description = this.description ?: "",
        review = "",
        imageURL = this.imageUrl ?: "",
        date = this.releaseDate,
        timeline = this.timeline?.toDouble() ?: 10000.0,
        amazonLink = "",
        amazonStream = "",
        publisher = companyMap[this.publisher] ?: -1.also {
            Log.d(
                "holo-client",
                "failed to map company ${this.publisher}"
            )
        },
    )

    private suspend fun getSeriesMap() =
        database.daoSeries.getAllSeries().associate { it.title to it.id }

    private suspend fun getCompanyMap(): Map<Company, Int> = try {
        val dtoCompanies = database.daoCompany.getAllCompanies()
        Log.d("holo-client", "company DTOs: $dtoCompanies")
        Company.values().associateWith { company ->
            Log.d("holo-client", "mapping company: $company")
            val text = Json.encodeToString(company).trimQuotes()
            val dtoCompany = dtoCompanies.firstOrNull { it.companyName == text }

            dtoCompany?.id ?: database.daoCompany.insert(CompanyDto(companyName = text)).toInt()
        }
    } catch (e: Exception) {
        Log.e("UpdateMediaDatabase", "error getting company map: $e")
        emptyMap()
    }

    private suspend fun getTypeMap(): Map<MediaType, Int> = try {
        val dtoTypes = database.daoType.getAllMediaTypes()
        Log.d("holo-client", "media type DTOs: $dtoTypes")
        MediaType.values().associateWith { mediaType ->
            Log.d("holo-client", "mapping type: $mediaType")
            val text = Json.encodeToString(mediaType).trimQuotes()
            val dtoType = dtoTypes.firstOrNull { it.text == text }

            dtoType?.id ?: database.daoType.insert(MediaTypeDto().apply { setText(text) }).toInt()
        }
    } catch (e: Exception) {
        Log.e("UpdateMediaDatabase", "error getting type map: $e")
        emptyMap()
    }

    private fun String.trimQuotes() = this.trim('\"')
}
