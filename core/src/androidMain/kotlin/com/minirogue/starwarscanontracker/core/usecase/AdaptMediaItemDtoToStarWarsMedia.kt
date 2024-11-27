package com.minirogue.starwarscanontracker.core.usecase

import android.util.Log
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.minirogue.common.model.Company
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Reusable
class AdaptMediaItemDtoToStarWarsMedia @Inject internal constructor(
    private val daoCompany: DaoCompany,
    private val daoSeries: DaoSeries,
    private val json: Json,
    private val dispatchers: HolocanonDispatchers,
) {
    private val adapterScope = CoroutineScope(Job() + dispatchers.default)
    private val companyMap: SharedFlow<Map<Int, Company>> = daoCompany.getAllCompanies()
        .map { list ->
            list.associate {
                it.id to try {
                    json.decodeFromString<Company>("\"${it.companyName}\"")
                } catch (e: SerializationException) {
                    Log.e(TAG, "couldn't decode persisted company: ${it.companyName}", e)
                    Company.DISNEY
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "couldn't decode persisted company: ${it.companyName}", e)
                    Company.DISNEY
                }            }
        }
        .shareIn(scope = adapterScope, started = SharingStarted.Lazily, replay = 1)

    private val seriesMap: SharedFlow<Map<Int, String>> = daoSeries.getAllSeries()
        .map { list -> list.associate { it.id to it.title } }
        .shareIn(scope = adapterScope, started = SharingStarted.Lazily, replay = 1)

    suspend operator fun invoke(mediaItemDto: MediaItemDto): StarWarsMedia = StarWarsMedia(
        id = mediaItemDto.id.toLong(),
        title = mediaItemDto.title,
        type = MediaType.getFromLegacyId(mediaItemDto.type),
        imageUrl = mediaItemDto.imageURL,
        releaseDate = mediaItemDto.date,
        timeline = mediaItemDto.timeline.toFloat(),
        description = mediaItemDto.description,
        series = seriesMap.first()[mediaItemDto.series],
        number = null,
        publisher = companyMap.first()[mediaItemDto.publisher] ?: Company.DISNEY.also {
            Log.e(TAG, "couldn't map publisher to a company: $this")
        },
    )
  companion object {
      private const val TAG = "GetMediaWithNotes"
  }
}
