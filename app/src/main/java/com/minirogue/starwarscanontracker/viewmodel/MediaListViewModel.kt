package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import android.util.SparseArray
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.model.PrefsRepo
import com.minirogue.starwarscanontracker.model.SortStyle
import com.minirogue.starwarscanontracker.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.model.repository.SeriesRepository
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MediaListViewModel @ViewModelInject constructor(private val repository: SWMRepository,
                                                      private val seriesRepository: SeriesRepository,
                                                      private val connMgr: MyConnectivityManager,
                                                      prefsRepo: PrefsRepo,
                                                      application: Application) : ViewModel() {

    //filtering
    val activeFilters = repository.getActiveFilters()

    //The data requested by the user
    private var data: LiveData<List<MediaAndNotes>> = MutableLiveData()
    private val sortedData = MediatorLiveData<List<MediaAndNotes>>()
    val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
        get() = sortedData
    private val mediaTypeToString = SparseArray<String>()
    private val dataMediator = MediatorLiveData<List<MediaItem>>()

    //The current method of sorting
    private val _sortStyle = MutableLiveData<SortStyle>()
    val sortStyle: LiveData<SortStyle>
        get() = _sortStyle

    //Checkbox settings
    val checkBoxText = repository.getCheckBoxText()
    val checkBoxVisibility: LiveData<BooleanArray> = prefsRepo.checkBoxVisibility

    //Variables for handling exactly one query and sort job at a time
    private var queryJob: Job = Job()
    private val queryMutex = Mutex()
    private var sortJob: Job = Job()
    private val sortMutex = Mutex()

    //The file where the current sorting method is stored
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"

    //Whether or not network calls are currently allowed. Used for fetching images.
    fun isNetworkAllowed(): Boolean = connMgr.isNetworkAllowed()


    init {
        viewModelScope.launch { _sortStyle.postValue(getSavedSort()) }
        viewModelScope.launch(Dispatchers.Default) {
            val mediaTypes = repository.getAllMediaTypesNonLive()
            mediaTypes.forEach { mediaTypeToString.put(it.id, it.text) }
        }
        dataMediator.addSource(activeFilters) { viewModelScope.launch { updateQuery() } }
        dataMediator.addSource(repository.getAllFilterTypes()) { viewModelScope.launch { updateQuery() } }
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
            SortStyle(SortStyle.DEFAULT_STYLE, true)
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
                val newListLiveData = repository.getMediaListWithNotes(activeFilters.value?.map { it.filterObject }
                        ?: ArrayList())
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
                            ?: SortStyle(SortStyle.DEFAULT_STYLE, true))
                    sortedData.postValue(toBeSorted)
                }
            }
        }
    }

    fun update(mediaNotes: MediaNotes) {
        repository.update(mediaNotes)
    }

    fun convertTypeToString(typeId: Int): String {
        return mediaTypeToString[typeId, ""]
    }

    suspend fun getSeriesString(seriesId: Int): String = seriesRepository.getSeriesStringById(seriesId)

    fun deactivateFilter(filterObject: FilterObject) = viewModelScope.launch(Dispatchers.Default) {
        filterObject.active = false
        repository.update(filterObject)
    }

    /*companion object {
        private const val TAG = "MediaListViewModel"
    }*/
}
