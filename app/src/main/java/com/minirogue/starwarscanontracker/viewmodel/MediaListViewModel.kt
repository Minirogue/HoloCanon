package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import com.minirogue.starwarscanontracker.usecase.GetCheckboxText
import com.minirogue.starwarscanontracker.usecase.GetMediaListWithNotes
import com.minirogue.starwarscanontracker.usecase.UpdateNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.GetActiveFilters
import filters.GetAllFilterTypes
import filters.UpdateFilter
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import settings.usecase.GetCheckboxSettings
import java.io.File
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class MediaListViewModel @Inject constructor(
        getActiveFilters: GetActiveFilters,
        getAllFilterTypes: GetAllFilterTypes,
        private val updateFilter: UpdateFilter,
        getCheckboxText: GetCheckboxText,
        private val updateNotes: UpdateNotes,
        private val getMediaListWithNotes: GetMediaListWithNotes,
        connMgr: MyConnectivityManager,
        getCheckboxSettings: GetCheckboxSettings,
        application: Application,
) : ViewModel() {

    // filtering
    val activeFilters = getActiveFilters().asLiveData(viewModelScope.coroutineContext)

    // The data requested by the user
    private var data: LiveData<List<MediaAndNotes>> = MutableLiveData()
    private val sortedData = MediatorLiveData<List<MediaAndNotes>>()
    val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
        get() = sortedData
    private val mediaTypeToString = SparseArray<String>()
    private val dataMediator = MediatorLiveData<List<MediaItemDto>>()

    // The current method of sorting
    private val _sortStyle = MutableLiveData<SortStyle>()
    val sortStyle: LiveData<SortStyle>
        get() = _sortStyle

    // Checkbox settings
    val checkBoxText = getCheckboxText.invoke()
    val checkBoxVisibility: Flow<BooleanArray> = getCheckboxSettings().map { checkboxSettings ->
        booleanArrayOf(
                checkboxSettings.checkbox1Setting.isInUse,
                checkboxSettings.checkbox2Setting.isInUse,
                checkboxSettings.checkbox3Setting.isInUse,
        )
    }

    // Whether or not network calls are currently allowed. Used for fetching images.
    val isNetworkAllowed: StateFlow<Boolean> = connMgr.isNetworkAllowed()
            .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)

    // Variables for handling exactly one query and sort job at a time
    private var queryJob: Job = Job()
    private val queryMutex = Mutex()
    private var sortJob: Job = Job()
    private val sortMutex = Mutex()

    // The file where the current sorting method is stored
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"

    init {
        viewModelScope.launch { _sortStyle.postValue(getSavedSort()) }
        viewModelScope.launch(Dispatchers.Default) {
            MediaType.entries.forEach { mediaTypeToString.put(it.legacyId, it.getSerialName()) }
        }
        dataMediator.addSource(activeFilters) { viewModelScope.launch { updateQuery() } }
        dataMediator.addSource(
                getAllFilterTypes().asLiveData(viewModelScope.coroutineContext)
        ) { viewModelScope.launch { updateQuery() } }
        sortedData.addSource(sortStyle) { viewModelScope.launch { sort(); saveSort() } }
        // dataMediator needs to be observed so the things it observes can trigger events
        sortedData.addSource(dataMediator) { }
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

    private suspend fun getSavedSort(): SortStyle = withContext(Dispatchers.IO) {
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
                val newListLiveData = getMediaListWithNotes(activeFilters.value ?: emptyList())
                withContext(Dispatchers.Main) {
                    dataMediator.removeSource(data)
                    data = newListLiveData
                    dataMediator.addSource(data) { viewModelScope.launch { sort() } }
                }
            }
        }
    }

    private suspend fun sort() = withContext(Dispatchers.Default) {
        sortMutex.withLock {
            sortJob.cancelAndJoin()
            sortJob = launch {
                val toBeSorted = data.value
                if (toBeSorted != null) {
                    val sorted = toBeSorted.sortedWith(
                            _sortStyle.value
                                    ?: SortStyle(SortStyle.DEFAULT_STYLE, true)
                    )
                    sortedData.postValue(sorted)
                }
            }
        }
    }

    fun update(mediaNotesDto: MediaNotesDto) {
        updateNotes(mediaNotesDto)
    }

    fun convertTypeToString(typeId: Int): String {
        return mediaTypeToString[typeId, ""]
    }

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
