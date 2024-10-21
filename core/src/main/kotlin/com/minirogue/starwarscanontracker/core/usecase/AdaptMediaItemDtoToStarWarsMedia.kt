package com.minirogue.starwarscanontracker.core.usecase

import android.util.Log
import com.minirogue.common.model.Company
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Reusable
class AdaptMediaItemDtoToStarWarsMedia @Inject internal constructor(
    private val daoCompany: DaoCompany,
    private val daoSeries: DaoSeries,
    private val json: Json,
) {
    private var companyMap: Flow<Map<Int, Company>> = daoCompany.getAllCompanies()
        .map { list ->
            list.associate {
                it.id to json.decodeFromString<Company>("\"${it.companyName}\"")
            }
        }
    private var seriesMap: Flow<Map<Int, String>> = daoSeries.getAllSeries()
        .map { list -> list.associate { it.id to it.title } }

    suspend operator fun invoke(mediaItemDto: MediaItemDto): StarWarsMedia = StarWarsMedia(
        id = mediaItemDto.id.toLong(),
        title = mediaItemDto.title,
        type = MediaType.getFromLegacyId(mediaItemDto.type) ?: MediaType.REFERENCE,
        imageUrl = mediaItemDto.imageURL,
        releaseDate = mediaItemDto.date,
        timeline = mediaItemDto.timeline.toFloat(),
        description = mediaItemDto.description,
        series = seriesMap.first()[mediaItemDto.series],
        number = null,
        publisher = companyMap.first()[mediaItemDto.publisher] ?: Company.DISNEY_LUCASFILMS.also {
            Log.e("GetMediaWithNotes", "couldn't map publisher to a company: $this")
        },
    )
}
