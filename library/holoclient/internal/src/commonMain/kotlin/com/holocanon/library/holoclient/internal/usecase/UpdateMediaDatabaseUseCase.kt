package com.holocanon.library.holoclient.internal.usecase

import com.holocanon.core.data.dao.DaoCompany
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.data.dao.DaoSeries
import com.holocanon.core.data.entity.CompanyDto
import com.holocanon.core.data.entity.MediaItemDto
import com.holocanon.core.data.entity.MediaNotesDto
import com.holocanon.feature.global.notification.usecase.SendInAppNotification
import com.holocanon.library.filters.usecase.UpdateFilters
import com.holocanon.library.holoclient.internal.api.GetApiMediaVersion
import com.holocanon.library.holoclient.internal.api.GetMediaFromApi
import com.holocanon.library.logger.HoloLogger
import com.holocanon.library.networking.HoloResult
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import com.minirogue.common.model.Company
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import holocanon.library.holoclient.internal.generated.resources.Res
import holocanon.library.holoclient.internal.generated.resources.holoclient_database_synced
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString
import settings.usecase.GetAllSettings
import settings.usecase.SetLatestDatabaseVersion

@Inject
@ContributesBinding(AppScope::class)
class UpdateMediaDatabaseUseCase internal constructor(
    private val updateFilters: UpdateFilters,
    private val getApiMediaVersion: GetApiMediaVersion,
    private val getMediaFromApi: GetMediaFromApi,
    private val getSettings: GetAllSettings,
    private val setLatestDatabaseVersion: SetLatestDatabaseVersion,
    private val daoMedia: DaoMedia,
    private val daoSeries: DaoSeries,
    private val daoCompany: DaoCompany,
    private val json: Json,
    private val sendInAppNotification: SendInAppNotification,
    private val logger: HoloLogger,
    private val isNetworkAllowed: IsNetworkAllowed,
) : MaybeUpdateMediaDatabase {
    override fun invoke(forced: Boolean) {
        usecaseScope.launch {
            mutex.withLock {
                val settings = getSettings().first()
                val latestLocalVersion = settings.latestDatabaseVersion
                if (!isNetworkAllowed().first()) {
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
                    sendInAppNotification(getString(Res.string.holoclient_database_synced))
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
        daoSeries.getAllSeries().first().associate { it.title to it.id }

    private suspend fun getCompanyMap(): Map<Company, Int> = try {
        val dtoCompanies = daoCompany.getAllCompanies().first()
        Company.entries.associateWith { company ->
            val text = json.encodeToString(company).trimQuotes()
            val dtoCompany = dtoCompanies.firstOrNull { it.companyName == text }

            dtoCompany?.id ?: daoCompany.insert(CompanyDto(companyName = text))
                .toInt()
        }
    } catch (e: Exception) {
        logger.error(TAG, "error getting company map:", e)
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
