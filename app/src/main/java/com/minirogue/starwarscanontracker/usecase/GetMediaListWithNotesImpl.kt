package com.minirogue.starwarscanontracker.usecase

import android.util.SparseBooleanArray
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minirogue.api.media.MediaType
import com.minirogue.api.media.StarWarsMedia
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.usecase.GetMediaListWithNotes
import filters.model.FilterType
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import settings.usecase.GetPermanentFilterSettings
import javax.inject.Inject

class GetMediaListWithNotesImpl @Inject constructor(
        private val daoMedia: DaoMedia,
        private val daoFilter: DaoFilter,
        private val getPermanentFilterSettings: GetPermanentFilterSettings,
) : GetMediaListWithNotes {
    /**
     * Returns LiveData containing a list of MediaAndNotes based on the given filters.
     *
     * Uses the given filters to return a list of combined MediaItems and MediaNotes (as MediaAndNotes)
     * in a LiveData object.
     *
     * @param filterList the list of Filters to apply to the query
     */
    override suspend fun invoke(filterList: List<MediaFilter>): Flow<List<MediaAndNotes>> {
        val query = convertFiltersToQuery(filterList)
        return daoMedia.getMediaAndNotesRawQuery(query).map { list ->
            list.map {
                MediaAndNotes(it.mediaItemDto)
            }
        }
    }

    private fun MediaItemDto.toStarWarsMedia(): StarWarsMedia = StarWarsMedia(id = id.toLong(), title = title, type = Mediatype, imageUrl =, releaseDate =, timeline =, description =, series =, number =, publisher =)

    // TODO
    @Suppress("LongMethod", "ComplexMethod", "BlockingMethodInNonBlockingContext")
    /**
     * Returns a SimpleSQLiteQuery based on the given filters and the stored permanent filters
     *
     * @param filterList the list of Filters to apply to the query
     */
    private suspend fun convertFiltersToQuery(filterList: List<MediaFilter>): SimpleSQLiteQuery = withContext(
            Dispatchers.Default) {
        val gettingPermanentFilters = async { getPermanentFiltersAsStringBuilder() }
        val filterTypeIsPositive = SparseBooleanArray()
        for (filterType in daoFilter.getAllFilterTypesNonLive()) {
            filterTypeIsPositive.put(filterType.typeId, filterType.isFilterPositive)
        }
        val queryBuild = StringBuilder()
        val joins = StringBuilder()
        val seriesFilter = StringBuilder()
        val typeFilter = StringBuilder()
        val publisherFilter = StringBuilder()
        val notesFilter = StringBuilder()
        for (filter in filterList) {
            if (filter.isActive) {
                when (filter.filterType) {
                    FilterType.Series -> {
                        if (seriesFilter.isEmpty()) {
                            if (!filter.isPositive) {
                                seriesFilter.append(" NOT ")
                            }
                        } else {
                            if (!filter.isPositive) {
                                seriesFilter.append(" AND NOT ")
                            } else {
                                seriesFilter.append(" OR ")
                            }
                        }
                        seriesFilter.append(" series = ")
                        seriesFilter.append(filter.id)
                    }

                    FilterType.MediaType -> {
                        if (typeFilter.isEmpty()) {
                            if (!filter.isPositive) {
                                typeFilter.append(" NOT ")
                            }
                        } else {
                            if (!filter.isPositive) {
                                typeFilter.append(" AND NOT ")
                            } else {
                                typeFilter.append(" OR ")
                            }
                        }
                        typeFilter.append(" type = ")
                        typeFilter.append(filter.id)
                    }

                    FilterType.Publisher -> {
                        if (publisherFilter.isEmpty()) {
                            if (!filter.isPositive) {
                                publisherFilter.append(" NOT ")
                            }
                        } else {
                            if (!filter.isPositive) {
                                publisherFilter.append(" AND NOT ")
                            } else {
                                publisherFilter.append(" OR ")
                            }
                        }
                        publisherFilter.append(" publisher = ${filter.id} ")
                    }

                    FilterType.CheckboxThree -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.checkbox_3 = 1 ")
                    }

                    FilterType.CheckboxOne -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.checkbox_1 = 1 ")
                    }

                    FilterType.CheckboxTwo -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.checkbox_2 = 1 ")
                    }
                }
            }
        }
        queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items " +
                "INNER JOIN media_notes ON media_items.id = media_notes.media_id ")
        queryBuild.append(joins)
        var whereClause = false
        if (seriesFilter.isNotEmpty()) {
            queryBuild.append(if (whereClause) " AND (" else " WHERE (")
            whereClause = true
            queryBuild.append(seriesFilter)
            queryBuild.append(")")
        }
        if (typeFilter.isNotEmpty()) {
            queryBuild.append(if (whereClause) " AND (" else " WHERE (")
            whereClause = true
            queryBuild.append(typeFilter)
            queryBuild.append(")")
        }
        if (publisherFilter.isNotEmpty()) {
            queryBuild.append(if (whereClause) " AND (" else " WHERE (")
            whereClause = true
            queryBuild.append(publisherFilter)
            queryBuild.append(")")
        }
        if (notesFilter.isNotEmpty()) {
            queryBuild.append(if (whereClause) " AND (" else " WHERE (")
            whereClause = true
            queryBuild.append(notesFilter)
            queryBuild.append(")")
        }
        val permanentFilters = gettingPermanentFilters.await()
        if (permanentFilters.isNotEmpty()) {
            queryBuild.append(if (whereClause) " AND (" else " WHERE (")

            whereClause = true
            queryBuild.append(permanentFilters)
            queryBuild.append(")")
        }
        SimpleSQLiteQuery(queryBuild.toString())
    }

    /**
     * Returns a StringBuilder to apply to a query for filtering out MediaTypes that have been marked as
     * "permanently filtered" in settings.
     *
     * Checks the DefaultSharedPreferences for permanently filtered MediaTypes then returns a StringBuilder
     * of "AND NOT type = " statements for use in a room query.
     */
    private suspend fun getPermanentFiltersAsStringBuilder(): StringBuilder = withContext(Dispatchers.IO) {
        val permFiltersBuilder = StringBuilder()
        val permanentFilterSettings = getPermanentFilterSettings().first()
        for (type in MediaType.entries) {
            if (permanentFilterSettings[type] == false) {
                if (permFiltersBuilder.isNotEmpty()) {
                    permFiltersBuilder.append(" AND ")
                }
                permFiltersBuilder.append(" NOT type = ")
                permFiltersBuilder.append(type.legacyId)
            }
        }
        permFiltersBuilder
    }
}
