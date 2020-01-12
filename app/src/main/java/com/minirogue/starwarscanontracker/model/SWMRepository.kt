package com.minirogue.starwarscanontracker.model

import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.SparseBooleanArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minirogue.starwarscanontracker.model.room.dao.DaoFilter
import com.minirogue.starwarscanontracker.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.model.room.dao.DaoSeries
import com.minirogue.starwarscanontracker.model.room.dao.DaoType
import com.minirogue.starwarscanontracker.model.room.entity.*
import com.minirogue.starwarscanontracker.model.room.pojo.FullFilter
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.ref.WeakReference
import javax.inject.Inject

class SWMRepository @Inject constructor(val daoMedia: DaoMedia,
                                        val daoType: DaoType,
                                        val daoFilter: DaoFilter,
                                        val daoSeries: DaoSeries,
                                        val sharedPreferences: SharedPreferences) {
    //private val TAG = "Repo"


    //A Mutex in case notes are being updated concurrently (e.g. user clicks on two separate checkboxes for a series)
    private val updatingNotesMutex = Mutex()

    /**
     * Returns LiveData containing a list of MediaAndNotes based on the given filters.
     *
     * Uses the given filters to return a list of combined MediaItems and MediaNotes (as MediaAndNotes)
     * in a LiveData object.
     *
     * @param filterList the list of Filters to apply to the query
     */
    suspend fun getMediaListWithNotes(filterList: List<FilterObject>): LiveData<List<MediaAndNotes>> {
        val query = convertFiltersToQuery(filterList)
        return daoMedia.getMediaAndNotesRawQuery(query)
    }

    /**
     * Returns a SimpleSQLiteQuery based on the given filters and the stored permanent filters
     *
     * @param filterList the list of Filters to apply to the query
     */
    private suspend fun convertFiltersToQuery(filterList: List<FilterObject>): SimpleSQLiteQuery = withContext(Dispatchers.Default) {
        val gettingPermanentFilters = async { getPermanentFiltersAsStringBuilder() }
        val filterTypeIsPositive = SparseBooleanArray()
        for (filterType in daoFilter.getAllFilterTypesNonLive()) {
            filterTypeIsPositive.put(filterType.typeId, filterType.isFilterPositive)
        }
        val queryBuild = StringBuilder()
        val joins = StringBuilder()
        //val characterFilter = StringBuilder()
        val seriesFilter = StringBuilder()
        val typeFilter = StringBuilder()
        val notesFilter = StringBuilder()
        for (filter in filterList) {
            if (filter.active) {
                when (filter.filterType) {
                    /* FilterType.FILTERCOLUMN_CHARACTER -> {
                         if (characterFilter.isEmpty()) {
                             joins.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId ")
                         } else {
                             characterFilter.append(" AND ")
                         }
                         if (!filterTypeIsPositive[filter.filterType]) {
                             characterFilter.append(" NOT ")
                         }
                         characterFilter.append(" media_character_join.characterID = ")
                         characterFilter.append(filter.id)
                     }*/
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
                    FilterType.FILTERCOLUMN_CHECKBOX_THREE -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.owned = 1 ")
                    }
                    FilterType.FILTERCOLUMN_CHECKBOX_ONE -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.watched_or_read = 1 ")
                    }
                    FilterType.FILTERCOLUMN_CHECKBOX_TWO -> {
                        if (notesFilter.isNotEmpty()) {
                            notesFilter.append(" AND ")
                        }
                        if (!filterTypeIsPositive[filter.filterType]) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.want_to_watch_or_read = 1 ")
                    }
                }
            }
        }
        queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId ")
        queryBuild.append(joins)
        var whereClause = false
        /*if (characterFilter.isNotEmpty()) {

            queryBuild.append(if (whereClause) " AND (" else " WHERE (")
            whereClause = true
            queryBuild.append(characterFilter)
            queryBuild.append(")")
        }*/
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
        //}
    }

    /**
     * Returns a LiveData of a List of all FilterType objects
     */
    fun getAllFilterTypes(): LiveData<List<FilterType>> = daoFilter.getAllFilterTypes()

    fun getAllMediaTypesNonLive(): List<MediaType> = daoType.allNonLive


    /**
     * Returns LiveData containing the MediaItem corresponding to the given id.
     *
     * @param itemId the id for the desired MediaItem
     */
    fun getLiveMediaItem(itemId: Int): LiveData<MediaItem> {
        return daoMedia.getMediaItemById(itemId)
    }

    /**
     * Returns LiveData containing a List of MediaItems belonging to the Series with seriesId
     */
    fun getLiveNotesBySeries(seriesId: Int): LiveData<List<MediaNotes>> {
        return daoMedia.getMediaNotesBySeries(seriesId)
    }

    /**
     * Returns LiveData containing the Series corresponding the the seriesId
     */
    fun getLiveSeries(seriesId: Int): LiveData<Series> {
        return daoSeries.getLiveSeries(seriesId)
    }

    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    fun getLiveMediaNotes(itemId: Int): LiveData<MediaNotes> {
        return daoMedia.getMediaNotesById(itemId)
    }

    fun setSeriesWantToWatchRead(seriesId: Int, newValue: Boolean) = GlobalScope.launch(Dispatchers.Default) {
        updatingNotesMutex.withLock {
            val listOfNotes = daoMedia.getMediaNotesBySeriesNonLive(seriesId)
            for (notes in listOfNotes) {
                notes.isWantToWatchRead = newValue
                daoMedia.update(notes)
            }
        }
    }

    fun setSeriesOwned(seriesId: Int, newValue: Boolean) = GlobalScope.launch(Dispatchers.Default) {
        updatingNotesMutex.withLock {
            val listOfNotes = daoMedia.getMediaNotesBySeriesNonLive(seriesId)
            for (notes in listOfNotes) {
                notes.isOwned = newValue
                daoMedia.update(notes)
            }
        }
    }

    fun setSeriesWatchedRead(seriesId: Int, newValue: Boolean) = GlobalScope.launch(Dispatchers.Default) {
        updatingNotesMutex.withLock {
            val listOfNotes = daoMedia.getMediaNotesBySeriesNonLive(seriesId)
            for (notes in listOfNotes) {
                notes.isWatchedRead = newValue
                daoMedia.update(notes)
            }
        }
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
        for (type in daoType.allNonLive) {
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

    private suspend fun getPermanentFilters(): List<FilterObject> = withContext(Dispatchers.IO) {
        val filterList = ArrayList<FilterObject>()
        for (type in daoType.allNonLive) {
            if (!sharedPreferences.getBoolean(type.text, true)) {
                filterList.add(daoFilter.getFilter(type.id, FilterType.FILTERCOLUMN_TYPE)
                        ?: FilterObject(-1, -1, false, ""))
            }
        }
        filterList
    }


    /**
     * Update a MediaNotes entry in the room.
     *
     * @param mediaNotes the MediaNotes object to be updated
     */
    fun update(mediaNotes: MediaNotes?) {
        if (mediaNotes != null) {
            UpdateMediaNotes(daoMedia).execute(mediaNotes)
        }
    }

    /**
     * Persist a FilterObject to the database.
     */
    fun update(filterObject: FilterObject) = GlobalScope.launch(Dispatchers.Default) {
        daoFilter.update(filterObject)
    }

    /**
     * Persist a FilterType to the database.
     */
    fun update(filterType: FilterType) = GlobalScope.launch(Dispatchers.Default) {
        daoFilter.update(filterType)
    }

    fun getActiveFilters(): LiveData<List<FullFilter>> = liveData(Dispatchers.Default) {
        val permFilters = getPermanentFilters()
        val source: LiveData<List<FullFilter>> = Transformations.map(daoFilter.getActiveFilters()) {
            val newList = ArrayList<FullFilter>()
            for (fullFilter in it) {
                if (fullFilter.filterObject !in permFilters) {
                    newList.add(fullFilter)
                }
            }
            newList
        }
        emitSource(source)
    }

    fun getFiltersOfType(typeId: Int): LiveData<List<FilterObject>> = liveData(Dispatchers.Default) {
        if (typeId == FilterType.FILTERCOLUMN_TYPE) {
            val permFilters = getPermanentFilters()
            val source: LiveData<List<FilterObject>> = Transformations.map(daoFilter.getFiltersWithType(typeId)) {
                val newList = ArrayList<FilterObject>()
                for (filterObject in it) {
                    if (filterObject !in permFilters) {
                        newList.add(filterObject)
                    }
                }
                newList
            }
            emitSource(source)
        } else {
            emitSource(daoFilter.getFiltersWithType(typeId))
        }
    }


    fun getCheckBoxText(): LiveData<Array<String>> {
        return Transformations.map(daoFilter.getCheckBoxFilterTypes()) { filterTypeList ->
            val checkboxTextArr = arrayOf("", "", "")
            filterTypeList.forEach {
                when (it.typeId) {
                    FilterType.FILTERCOLUMN_CHECKBOX_ONE -> checkboxTextArr[0] = it.text
                    FilterType.FILTERCOLUMN_CHECKBOX_TWO -> checkboxTextArr[1] = it.text
                    FilterType.FILTERCOLUMN_CHECKBOX_THREE -> checkboxTextArr[2] = it.text
                }
            }
            checkboxTextArr
        }
    }

    //TODO convert this to a coroutine
    private class UpdateMediaNotes internal constructor(daoMedia: DaoMedia) : AsyncTask<MediaNotes, Void, Void>() {
        internal var wrDaoMedia: WeakReference<DaoMedia> = WeakReference(daoMedia)

        override fun doInBackground(vararg mediaNotes: MediaNotes): Void? {
            wrDaoMedia.get()?.update(mediaNotes[0])
            return null
        }
    }

}
