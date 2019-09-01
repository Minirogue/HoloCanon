package com.minirogue.starwarscanontracker

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.lifecycle.*
import com.minirogue.starwarscanontracker.database.MediaAndNotes
import com.minirogue.starwarscanontracker.database.MediaItem
import com.minirogue.starwarscanontracker.database.MediaNotes
import com.minirogue.starwarscanontracker.database.SWMRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

internal class MediaListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SWMRepository = SWMRepository(application)
    val filters = MutableLiveData<MutableList<FilterObject>>()
    var allFilters: List<FilterObject> = ArrayList() //= FilterObject.getAllFilters(application) //TODO this can be managed better
    private var data: LiveData<List<MediaAndNotes>> = MutableLiveData()
    private val dataMediator = MediatorLiveData<List<MediaItem>>()
    private val sortedData = MediatorLiveData<List<MediaAndNotes>>()
    private val checkboxText: Array<String>
    private val sortStyle = MutableLiveData<SortStyle>()
    private val connMgr: ConnectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val unmeteredOnly: Boolean
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"
    //private val sortQueue = OperationQueue()
    private var queryJob = Job()
    private val queryMutex = Mutex()
    private var sortJob = Job()
    private val sortMutex = Mutex()


    val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
        get() = sortedData
    val isNetworkAllowed: Boolean
        get() = !connMgr.isActiveNetworkMetered || !unmeteredOnly

    init {
        viewModelScope.launch { sortStyle.postValue(getSavedSort()) }
        viewModelScope.launch {
            val savedFilters = async { repository.getSavedFilters() }
            val everyFilter = async { repository.getAllFilters() }
            val currentFilters = savedFilters.await()
            allFilters = everyFilter.await()
            for (thisfilter in currentFilters) {//TODO fix inefficient looping
                for (genericfilter in allFilters) {
                    if (thisfilter == genericfilter) {
                        thisfilter.displayText = genericfilter.displayText
                        break
                    }
                }
            }
            filters.postValue(currentFilters)
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        checkboxText = arrayOf(prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read)),
                prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read)),
                prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned)))
        unmeteredOnly = prefs.getBoolean(application.getString(R.string.setting_unmetered_sync_only), true)

        dataMediator.addSource(filters) { viewModelScope.launch { updateQuery() } }
        sortedData.addSource(sortStyle) { viewModelScope.launch { sort(); saveSort() } }
        sortedData.addSource(dataMediator) { }//dataMediator needs to be observed so the things it observes can trigger events
    }

    fun setSort(newCompareType: Int) {
        sortStyle.postValue(SortStyle(newCompareType, true))
    }


    private suspend fun saveSort() = withContext(Dispatchers.IO) {
        val style = sortStyle.value
        if (style != null) {
            val cacheFile = File(sortCacheFileName)
            cacheFile.writeText(style.style.toString() + " " + if (style.ascending) "1" else "0")
        }
    }

    private suspend fun getSavedSort(): SortStyle? = withContext(Dispatchers.IO) {
        val cacheFile = File(sortCacheFileName)
        if (!cacheFile.exists()) {
            SortStyle(SortStyle.SORT_TITLE, true)
        } else {
            val split = cacheFile.readText().split(" ")
            SortStyle(split[0].toInt(), split[1].toInt() == 1)
        }
    }


    fun reverseSort() {
        val currentStyle = sortStyle.value
        if (currentStyle != null) {
            sortStyle.postValue(currentStyle.reversed())
        }
    }

    fun checkForUpdatedPermFilters() {
        viewModelScope.launch(Dispatchers.IO) {
            val newAllFilters = repository.getAllFilters()
            if (allFilters != newAllFilters) {
                //Log.d(TAG, "perm filters marked as changed, updating query")
                allFilters = newAllFilters
                updateQuery()
            }
        }
    }

    private suspend fun updateQuery() = withContext(Dispatchers.IO) {
        queryMutex.withLock {
            queryJob.cancelAndJoin()
            queryJob = launch {
                val newListLiveData = repository.getMediaListWithNotes(filters.value ?: ArrayList())
                withContext(Dispatchers.Main) {
                    dataMediator.removeSource(data)
                    data = newListLiveData
                    dataMediator.addSource(data) { viewModelScope.launch { sort() } }
                }
            }
        }
    }

    private suspend fun sort() = withContext(Dispatchers.Default) {
        //Log.d(TAG, "Sort called");
        sortMutex.withLock {
            sortJob.cancelAndJoin()
            sortJob = launch {
                val toBeSorted = data.value
                if (toBeSorted != null) {
                    Collections.sort(toBeSorted, sortStyle.value
                            ?: SortStyle(SortStyle.SORT_TITLE, true))
                    sortedData.postValue(toBeSorted)
                }
            }
        }

    }

    fun getSortStyle(): LiveData<SortStyle> {
        return sortStyle
    }

    fun removeFilter(filter: FilterObject) = viewModelScope.launch(Dispatchers.Default) {
        val tempList = filters.value
        if (tempList != null) {
            tempList.remove(filter)
            filters.postValue(tempList)
            repository.saveFilters(tempList)
        }
    }

    fun addFilter(filter: FilterObject) = viewModelScope.launch(Dispatchers.Default) {
        val tempList = filters.value
        if (tempList != null) {
            tempList.add(filter)
            filters.postValue(tempList)
            repository.saveFilters(tempList)
        }
    }

    fun clearSavedFilters() {
        GlobalScope.launch { repository.clearSavedFilters() }
    }

    fun isCurrentFilter(filter: FilterObject): Boolean {
        val currentList = filters.value
        return currentList?.contains(filter) ?: false
    }

    fun update(mediaNotes: MediaNotes) {
        repository.update(mediaNotes)
    }

    fun convertTypeToString(typeId: Int): String {
        return repository.convertTypeToString(typeId)
    }


    fun getCheckboxText(boxNumber: Int): String {
        return checkboxText[boxNumber]
    }

    companion object {
        private const val TAG = "MediaListViewModel"
    }
}
