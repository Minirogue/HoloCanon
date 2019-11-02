package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.SortStyle
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.SWMRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

internal class MediaListViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val dataMediator = MediatorLiveData<List<MediaItem>>()
    private val repository: SWMRepository by inject()
    private val connMgr: ConnectivityManager by inject()
    private val checkboxText: Array<String> by inject(named("checkboxes"))
    private val unmeteredOnly: Boolean by inject(named("unmetered_only"))

    //filtering
    val activeFilters = repository.getActiveFilters()
    val filterTypes = repository.getAllFilterTypes()

    //The data requested by the user
    private var data: LiveData<List<MediaAndNotes>> = MutableLiveData()
    private val sortedData = MediatorLiveData<List<MediaAndNotes>>()
    val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
        get() = sortedData

    //The current method of sorting
    private val _sortStyle = MutableLiveData<SortStyle>()
    val sortStyle: LiveData<SortStyle>
        get() = _sortStyle

    //Variables for handling exactly one query and sort job at a time
    private var queryJob: Job = Job()
    private val queryMutex = Mutex()
    private var sortJob: Job = Job()
    private val sortMutex = Mutex()

    //The file where the current sorting method is stored
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"

    //Whether or not network calls are currently allowed. Used for fetching images.
    val isNetworkAllowed: Boolean
        get() = !connMgr.isActiveNetworkMetered || !unmeteredOnly


    init {
        viewModelScope.launch { _sortStyle.postValue(getSavedSort()) }
        dataMediator.addSource(activeFilters) { viewModelScope.launch { updateQuery() } }
        dataMediator.addSource(filterTypes) { viewModelScope.launch { updateQuery() } }
        sortedData.addSource(sortStyle) { viewModelScope.launch { sort(); saveSort() } }
        sortedData.addSource(dataMediator) { }//dataMediator needs to be observed so the things it observes can trigger events
    }

    fun setSort(newCompareType: Int) {
        _sortStyle.postValue(SortStyle(newCompareType, true))
    }

    private suspend fun saveSort() = withContext(Dispatchers.IO) {
        val style = _sortStyle.value
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
        val currentStyle = _sortStyle.value
        if (currentStyle != null) {
            _sortStyle.postValue(currentStyle.reversed())
        }
    }

    private suspend fun updateQuery() = withContext(Dispatchers.IO) {
        queryMutex.withLock {
            queryJob.cancelAndJoin()
            queryJob = launch {
                val newListLiveData = repository.getMediaListWithNotes(activeFilters.value ?: ArrayList())
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
                    Collections.sort(toBeSorted, _sortStyle.value
                            ?: SortStyle(SortStyle.SORT_TITLE, true))
                    sortedData.postValue(toBeSorted)
                }
            }
        }
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

    fun getFiltersOfType(type: Int): LiveData<List<FilterObject>> = repository.getFiltersOfType(type)

    fun swapFilterIsActive(filterObject: FilterObject) = viewModelScope.launch(Dispatchers.Default) {
        filterObject.active = !filterObject.active
        repository.update(filterObject)
    }

    fun deactivateFilter(filterObject: FilterObject) = viewModelScope.launch(Dispatchers.Default) {
        filterObject.active = false
        repository.update(filterObject)
    }

    fun switchFilterType(filterType: FilterType) = viewModelScope.launch(Dispatchers.Default) {
        filterType.isFilterPositive = !filterType.isFilterPositive
        repository.update(filterType)
    }

    /*companion object {
        private const val TAG = "MediaListViewModel"
    }*/
}
