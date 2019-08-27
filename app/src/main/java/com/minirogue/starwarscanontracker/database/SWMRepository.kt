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
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class SWMRepository(private val application: Application) {
    private val TAG = "Repo"
    private val daoMedia: DaoMedia
    private val daoType: DaoType
    //val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
    //private val filters = MediatorLiveData<MutableList<FilterObject>>()
    //private val permanentFilters = MutableLiveData<StringBuilder>()
    //val allFilters: LiveData<List<FilterObject>>
    private val filterCacheFileName = application.cacheDir.toString() + "/filterCache"
    //private val filterTracker = MediatorLiveData<List<FilterObject>>()
    //private var currentQuery: SimpleSQLiteQuery? = null


    init {
        val db = MediaDatabase.getMediaDataBase(application)
        daoMedia = db.daoMedia
        daoType = db.daoType
        //allFilters = FilterObject.getAllFilters(application)
        //filters.value = ArrayList()
        //permanentFilters.value = StringBuilder()
        //filteredMediaAndNotes = Transformations.switchMap(filterTracker) { daoMedia.getMediaAndNotesRawQuery(currentQuery)}
//        GlobalScope.launch {
//            initializeFilters()
//        }
    }

    suspend fun getMediaListWithNotes(filterList : List<FilterObject>): LiveData<List<MediaAndNotes>>{
        val query = convertFiltersToQuery(filterList)
        return daoMedia.getMediaAndNotesRawQuery(query)
    }

   /* private suspend fun initializeFilters() = withContext(Dispatchers.IO) {
        //val permFilters = async { getPermanentFiltersAsStringBuilder() }
        val savedFilters = async { getSavedFilters() }
        val everyFilter = async { getAllFilters() }


        withContext(Dispatchers.Main) {
            filterTracker.addSource(filters) {
                launch(Dispatchers.Default) {
                    currentQuery = convertFiltersToQuery(it)
                    saveFilters(it)
                    filterTracker.postValue(it)
                }
            }
            filterTracker.addSource(permanentFilters) {
                launch(Dispatchers.Default) {
                    currentQuery = convertFiltersToQuery(filters.value)
                    filterTracker.postValue(filters.value)
                }
            }
        }
        val currentFilters = savedFilters.await()




        //filters.postValue(currentFilters)

        //permanentFilters.postValue(permFilters.await())
    }*/

    suspend fun getAllFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val typeFilters = async { makeTypeFilters() }
        val notesFilters = async { makeNotesFilters() }
        val allFilt = ArrayList<FilterObject>()
        allFilt.addAll(typeFilters.await())
        allFilt.addAll(notesFilters.await())
        allFilt
    }

    private suspend fun makeNotesFilters(): MutableList<FilterObject> = withContext(Dispatchers.IO) {
        val newNotesFilters = ArrayList<FilterObject>()
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_OWNED, prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned))))
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_HASREADWATCHED, prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read))))
        newNotesFilters.add(FilterObject(0, FilterObject.FILTERCOLUMN_WANTTOREADWATCH, prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read))))
        newNotesFilters
    }

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



    fun convertTypeToString(typeId: Int): String {
        return FilterObject.getTextForType(typeId)
    }

    fun getLiveMediaItem(itemId: Int): LiveData<MediaItem> {
        return daoMedia.getMediaItemById(itemId)
    }

    fun getLiveMediaNotes(itemId: Int): LiveData<MediaNotes> {
        return daoMedia.getMediaNotesById(itemId)
    }

    private suspend fun convertFiltersToQuery(filterList: List<FilterObject>): SimpleSQLiteQuery = withContext(Dispatchers.Default) {
        val gettingPermanentFilters = async {getPermanentFiltersAsStringBuilder()}
        if (filterList == null){
            SimpleSQLiteQuery("SELECT media_items.*,media_notes.* FROM media_items INNER JOIN media_notes ON media_items.id = media_notes.mediaId")
        }else {
            val queryBuild = StringBuilder()
            val joins = StringBuilder()
            val characterFilter = StringBuilder()
            val typeFilter = StringBuilder()
            val notesFilter = StringBuilder()
            for (filter in filterList) {
                when(filter.column) {
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
        }
    }

    private suspend fun getPermanentFiltersAsStringBuilder(): StringBuilder = withContext(Dispatchers.IO) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        val permFiltersBuilder = StringBuilder()
        for (type in daoType.allNonLive) {
            if (!prefs.getBoolean(type.text, true)) {
                if (permFiltersBuilder.isEmpty()) {
                } else {
                    permFiltersBuilder.append(" AND ")
                }
                permFiltersBuilder.append(" NOT type = ")
                permFiltersBuilder.append(type.id)
            }
        }
        permFiltersBuilder
    }
/*
    fun getFilters(): LiveData<MutableList<FilterObject>> {
        return filters
    }*/

    private suspend fun saveFilters(filtersToSave: List<FilterObject>) = withContext(Dispatchers.IO) {
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
                    val actualFilter = FilterObject(filterAsNumbers[1].toInt(), filterAsNumbers[0].toInt(), "t")
                    actualFilter.isPositive = filterAsNumbers[2].toInt() == 1
                    filterList.add(actualFilter)
                }
                filterList
            } else {
                ArrayList()
            }
        }
    }

    suspend fun clearSavedFilters() = withContext(Dispatchers.IO) {
        val cacheFile = File(filterCacheFileName)
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }

    fun update(mediaNotes: MediaNotes?) {
        if (mediaNotes != null) {
            UpdateMediaNotes(daoMedia).execute(mediaNotes)
        }
    }

    private class UpdateMediaNotes internal constructor(daoMedia: DaoMedia) : AsyncTask<MediaNotes, Void, Void>() {
        internal var wrDaoMedia: WeakReference<DaoMedia>

        init {
            wrDaoMedia = WeakReference(daoMedia)
        }

        override fun doInBackground(vararg mediaNotes: MediaNotes): Void? {
            wrDaoMedia.get()?.update(mediaNotes[0])
            return null
        }
    }

}
