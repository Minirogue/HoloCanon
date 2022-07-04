package com.minirogue.starwarscanontracker.usecase

import android.content.SharedPreferences
import android.util.SparseBooleanArray
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.core.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetMediaListWithNotes @Inject constructor(
    private val daoMedia: DaoMedia,
    private val daoFilter: DaoFilter,
    private val daoType: DaoType,
    private val sharedPreferences: SharedPreferences,
) {
    /**
     * Returns LiveData containing a list of MediaAndNotes based on the given filters.
     *
     * Uses the given filters to return a list of combined MediaItems and MediaNotes (as MediaAndNotes)
     * in a LiveData object.
     *
     * @param filterList the list of Filters to apply to the query
     */
    suspend operator fun invoke(filterList: List<FilterObject>): LiveData<List<MediaAndNotes>> {
        val query = convertFiltersToQuery(filterList)
        return daoMedia.getMediaAndNotesRawQuery(query)
    }

    // TODO
    @Suppress("LongMethod", "ComplexMethod", "BlockingMethodInNonBlockingContext")
    /**
     * Returns a SimpleSQLiteQuery based on the given filters and the stored permanent filters
     *
     * @param filterList the list of Filters to apply to the query
     */
    private suspend fun convertFiltersToQuery(filterList: List<FilterObject>): SimpleSQLiteQuery = withContext(
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
            if (filter.active) {
                when (filter.filterType) {
                    FilterType.FILTERCOLUMN_SERIES -> {
                        if (seriesFilter.isEmpty()) {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                seriesFilter.append(" NOT ")
                            }
                        } else {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                seriesFilter.append(" AND NOT ")
                            } else {
                                seriesFilter.append(" OR ")
                            }
                        }
                        seriesFilter.append(" series = ")
                        seriesFilter.append(filter.id)
                    }
                    FilterType.FILTERCOLUMN_TYPE -> {
                        if (typeFilter.isEmpty()) {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                typeFilter.append(" NOT ")
                            }
                        } else {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                typeFilter.append(" AND NOT ")
                            } else {
                                typeFilter.append(" OR ")
                            }
                        }
                        typeFilter.append(" type = ")
                        typeFilter.append(filter.id)
                    }
                    FilterType.FILTERCOLUMN_PUBLISHER -> {
                        if (publisherFilter.isEmpty()) {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                publisherFilter.append(" NOT ")
                            }
                        } else {
                            if (!filterTypeIsPositive[filter.filterType]) {
                                publisherFilter.append(" AND NOT ")
                            } else {
                                publisherFilter.append(" OR ")
                            }
                        }
                        publisherFilter.append(" publisher = ${filter.id} ")
                    }
                    FilterType.FILTERCOLUMN_CHECKBOX_THREE -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.checkbox_3 = 1 ")
                    }
                    FilterType.FILTERCOLUMN_CHECKBOX_ONE -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.checkbox_1 = 1 ")
                    }
                    FilterType.FILTERCOLUMN_CHECKBOX_TWO -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
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
        for (type in daoType.allNonLive()) {
            if (!sharedPreferences.getBoolean(type.text, true)) {
                if (permFiltersBuilder.isNotEmpty()) {
                    permFiltersBuilder.append(" AND ")
                }
                permFiltersBuilder.append(" NOT type = ")
                permFiltersBuilder.append(type.id)
            }
        }
        permFiltersBuilder
    }
}
