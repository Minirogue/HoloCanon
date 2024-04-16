package com.minirogue.starwarscanontracker.core.usecase

import android.util.Log
import com.minirogue.api.media.Company
import com.minirogue.api.media.MediaType
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoCompany
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class GetMediaAndNotesForSeries @Inject constructor(
    private val daoMedia: DaoMedia,
    private val daoCompany: DaoCompany,
    private val daoSeries: DaoSeries
) {
    operator fun invoke(seriesId: Int): Flow<List<MediaAndNotes>> {
        var reusableCompanyMap: Map<Int, Company>? = null
        var reusableSeriesMap: Map<Int, String>? = null

        return daoMedia.getMediaAndNotesForSeries(seriesId).map { list ->
            val companyMap: Map<Int, Company> = reusableCompanyMap
                ?: daoCompany.getAllCompanies().associate {
                    it.id to Json.decodeFromString<Company>("\"${it.companyName}\"")
                }.also { reusableCompanyMap = it }
            val seriesMap = reusableSeriesMap
                ?: daoSeries.getAllSeries().associate { it.id to it.title }
                    .also { reusableSeriesMap = it }
            list.map {
                MediaAndNotes(
                    it.mediaItemDto.toStarWarsMedia(companyMap, seriesMap),
                    it.mediaNotesDto.toMediaNotes()
                )
            }
        }
    }

    // TODO should centralize this adapter, as it is also used in GetMediaListWithNotesImpl
    private fun MediaItemDto.toStarWarsMedia(
        companyMap: Map<Int, Company>,
        seriesMap: Map<Int, String>
    ): StarWarsMedia = StarWarsMedia(
        id = id.toLong(),
        title = title,
        type = MediaType.getFromLegacyId(type) ?: MediaType.REFERENCE,
        imageUrl = imageURL,
        releaseDate = date,
        timeline = timeline.toFloat(),
        description = description,
        series = seriesMap[series],
        number = 1, // TODO
        publisher = companyMap[publisher] ?: Company.DISNEY_LUCASFILMS.also {
            Log.e("GetMediaWithNotes", "couldn't map publisher to a company: $this")
        },
    )
}
