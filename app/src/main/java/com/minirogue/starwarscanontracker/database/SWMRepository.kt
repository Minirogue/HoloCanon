package com.minirogue.starwarscanontracker.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.sqlite.db.SimpleSQLiteQuery
import com.minirogue.starwarscanontracker.FilterObject
import com.minirogue.starwarscanontracker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class SWMRepository(private val application: Application) {
    //private val TAG = "Repo"

    //The DAOs used to access the database
    private val daoMedia: DaoMedia
    private val daoType: DaoType

    //Variables to help manage saving of current filters
    private val filterCacheFileName = application.cacheDir.toString() + "/filterCache"
    private val saveFiltersMutex = Mutex()

    init {
        val db = MediaDatabase.getMediaDataBase(application)
        daoMedia = db.daoMedia
        daoType = db.daoType
    }

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
        /*if (filterList == null) {
            SimpleSQLiteQuery("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId")
        } else {*/
            val queryBuild = StringBuilder()
            val joins = StringBuilder()
            val characterFilter = StringBuilder()
            val typeFilter = StringBuilder()
            val notesFilter = StringBuilder()
            for (filter in filterList) {
                when (filter.column) {
                    FilterObject.FILTERCOLUMN_CHARACTER -> {
                        if (characterFilter.isEmpty()) {
                            joins.append(" INNER JOIN media_character_join ON media_items.id = media_character_join.mediaId ")
                        } else {
                            characterFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            characterFilter.append(" NOT ")
                        }
                        characterFilter.append(" media_character_join.characterID = ")
                        characterFilter.append(filter.id)
                    }
                    FilterObject.FILTERCOLUMN_TYPE -> {
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
                    FilterObject.FILTERCOLUMN_OWNED -> {
                        if (notesFilter.isEmpty()) {
                        } else {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.owned = 1 ")
                    }
                    FilterObject.FILTERCOLUMN_HASREADWATCHED -> {
                        if (notesFilter.isEmpty()) {
                        } else {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.watched_or_read = 1 ")
                    }
                    FilterObject.FILTERCOLUMN_WANTTOREADWATCH -> {
                        if (notesFilter.isEmpty()) {
                        } else {
                            notesFilter.append(" AND ")
                        }
                        if (!filter.isPositive) {
                            notesFilter.append(" NOT ")
                        }
                        notesFilter.append(" media_notes.want_to_watch_or_read = 1 ")
                    }
                }
            }
            queryBuild.append("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId ")
            queryBuild.append(joins)
            var whereClause = false
            if (characterFilter.isNotEmpty()) {

                queryBuild.append(if (whereClause) " AND (" else " WHERE (")
                whereClause = true
                queryBuild.append(characterFilter)
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
            //Log.d("ListAdapter", queryBuild.toString());
            SimpleSQLiteQuery(queryBuild.toString())
        //}
    }

    /**
     * Returns a List of all possible FilterObjects, except for ones listed as permanent filters
     */
    suspend fun getAllFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val typeFilters = async { makeTypeFilters() }
        val notesFilters = async { makeNotesFilters() }
        val allFilt = ArrayList<FilterObject>()
        allFilt.addAll(typeFilters.await())
        allFilt.addAll(notesFilters.await())
        allFilt
    }

    /**
     * Returns a list of FilterObjects corresponding to the fields in MediaNotes (which are the checkboxes
     * in the canon list screen).
     */
    private suspend fun makeNotesFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val newNotesFilters = ArrayList<FilterObject>()
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_OWNED, prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned))))
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_HASREADWATCHED, prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read))))
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_WANTTOREADWATCH, prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read))))
        newNotesFilters
    }

    /**
     * Returns a List containing FilterObjects corresponding to all MediaTypes except for those that have
     * been permanently filtered out.
     */
    private suspend fun makeTypeFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val newTypeFilters = ArrayList<FilterObject>()
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val allMediaTypes = daoType.allNonLive
        for (mediaType in allMediaTypes) {
            if (prefs.getBoolean(mediaType.text, true)) {
                newTypeFilters.add(FilterObject(mediaType.id, FilterObject.FILTERCOLUMN_TYPE, mediaType.text))
            }
        }
        newTypeFilters
    }

    /**
     * Converts a MediaType id to text for that type
     *
     * @param typeId the id corresponding to a MediaType
     */
    fun convertTypeToString(typeId: Int): String {
        return FilterObject.getTextForType(typeId)
    }

    /**
     * Returns LiveData containing the MediaItem corresponding to the given id.
     *
     * @param itemId the id for the desired MediaItem
     */
    fun getLiveMediaItem(itemId: Int): LiveData<MediaItem> {
        return daoMedia.getMediaItemById(itemId)
    }

    /**
     * Returns MediaNotes associated to the given MediaItem id
     *
     * @param itemId the id associated to the MediaItem for which the MediaNotes are desired
     */
    fun getLiveMediaNotes(itemId: Int): LiveData<MediaNotes> {
        return daoMedia.getMediaNotesById(itemId)
    }

    /**
     * Returns a StringBuilder to apply to a query for filtering out MediaTypes that have been marked as
     * "permanently filtered" in settings.
     *
     * Checks the DefaultSharedPreferences for permanently filtered MediaTypes then returns a StringBuilder
     * of "AND NOT type = " statements for use in a database query.
     */
    private suspend fun getPermanentFiltersAsStringBuilder(): StringBuilder = withContext(Dispatchers.IO) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val permFiltersBuilder = StringBuilder()
        for (type in daoType.allNonLive) {
            if (!prefs.getBoolean(type.text, true)) {
                if (permFiltersBuilder.isNotEmpty()) {
                    permFiltersBuilder.append(" AND ")
                }
                permFiltersBuilder.append(" NOT type = ")
                permFiltersBuilder.append(type.id)
            }
        }
        permFiltersBuilder
    }

    /**
     * Saves the given list of filters to the cache (only one set of filters saved at any time).
     *
     * Saves filters to a file as a comma separated list of space separated fields. Note that it does
     * not save the the displayText field.
     *
     * @param filtersToSave the List of FilterObjects to be saved.
     */
    suspend fun saveFilters(filtersToSave: List<FilterObject>) = withContext(Dispatchers.IO) {
        saveFiltersMutex.withLock {
            val cacheFile = File(filterCacheFileName)
            /*if (!cacheFile.exists()) {
                cacheFile.createNewFile()
            }*/
            val sbuild = StringBuilder()
            for (filter in filtersToSave) {
                if (sbuild.isNotEmpty()) {
                    sbuild.append(",")
                }
                sbuild.append(filter.column)
                sbuild.append(" ")
                sbuild.append(filter.id)
                sbuild.append(" ")
                sbuild.append(if (filter.isPositive) 1 else 0)
            }
            cacheFile.writeText(sbuild.toString())
        }
    }

    /**
     * Retrieves the current set of saved filters from the cache.
     *
     * Checks the cache for saved filters. If none exist, then it will return an empty list.
     * Otherwise, it returns a list of FilterObjects with 'FilterNotFound' as their displayText
     */
    suspend fun getSavedFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val cacheFile = File(filterCacheFileName)
        if (!cacheFile.exists()) {
            ArrayList()
        } else {
            val filtersAsText = cacheFile.readText()
            //Log.d(TAG, filtersAsText)
            if (filtersAsText.isNotBlank()) {
                val splitFilters = filtersAsText.split(",")
                val filterList = ArrayList<FilterObject>()
                for (textFilter in splitFilters) {
                    val filterAsNumbers = textFilter.split(" ")
                    val actualFilter = FilterObject(filterAsNumbers[1].toInt(), filterAsNumbers[0].toInt(), "FilterNotFound")
                    actualFilter.isPositive = filterAsNumbers[2].toInt() == 1
                    filterList.add(actualFilter)
                }
                filterList
            } else {
                ArrayList()
            }
        }
    }

    /**
     * Clears the cache of any saved filters.
     */
    suspend fun clearSavedFilters() = withContext(Dispatchers.IO) {
        val cacheFile = File(filterCacheFileName)
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }

    /**
     * Update a MediaNotes entry in the database.
     *
     * @param mediaNotes the MediaNotes object to be updated
     */
    fun update(mediaNotes: MediaNotes?) {
        if (mediaNotes != null) {
            UpdateMediaNotes(daoMedia).execute(mediaNotes)
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
