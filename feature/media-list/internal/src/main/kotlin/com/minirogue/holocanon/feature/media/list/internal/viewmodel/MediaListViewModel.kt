package com.minirogue.holocanon.feature.media.list.internal.viewmodel

import android.app.Application
import android.util.SparseArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotesDto
import com.minirogue.starwarscanontracker.core.usecase.GetMediaListWithNotes
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.UpdateNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.GetActiveFilters
import filters.GetAllFilterTypes
import filters.UpdateFilter
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import java.io.File
import javax.inject.Inject

data class MediaListState(
        val activeFilters: List<MediaFilter> = emptyList(),
        val sortStyle: SortStyle? = null,
        val checkboxSettings: CheckboxSettings = CheckboxSettings.NONE,
        val isNetworkAllowed: Boolean = false,
)

@Suppress("LongParameterList")
@HiltViewModel
internal class MediaListViewModel @Inject constructor(
        getActiveFilters: GetActiveFilters,
        getAllFilterTypes: GetAllFilterTypes,
        private val updateFilter: UpdateFilter,
        private val updateNotes: UpdateNotes,
        private val getMediaListWithNotes: GetMediaListWithNotes,
        isNetworkAllowed: IsNetworkAllowed,
        getCheckboxSettings: GetCheckboxSettings,
        application: Application,
) : ViewModel() {

    private val _state: MutableStateFlow<MediaListState> = MutableStateFlow(MediaListState())
    val state: StateFlow<MediaListState> = _state

    // The data requested by the user
    private var data: LiveData<List<MediaAndNotesDto>> = MutableLiveData()
    private val sortedData = MediatorLiveData<List<MediaAndNotesDto>>()
    val filteredMediaAndNotes: LiveData<List<MediaAndNotesDto>>
        get() = sortedData
    private val mediaTypeToString = SparseArray<String>()
    private val dataMediator = MediatorLiveData<List<MediaItemDto>>()

    // Variables for handling exactly one query and sort job at a time
    private var queryJob: Job = Job()
    private val queryMutex = Mutex()
    private var sortJob: Job = Job()
    private val sortMutex = Mutex()

    // The file where the current sorting method is stored
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"

    init {
        // filtering
        viewModelScope.launch {
            getActiveFilters().collect { activeFilters -> _state.update { it.copy(activeFilters = activeFilters) } }
        }
        viewModelScope.launch {
            getCheckboxSettings().collect { checkboxSettings -> _state.update { it.copy(checkboxSettings = checkboxSettings) } }
        }
        viewModelScope.launch {
            isNetworkAllowed().collect { networkAllowed -> _state.update { it.copy(isNetworkAllowed = networkAllowed) } }
        }
        viewModelScope.launch {
            val savedSort = getSavedSort()
            _state.update { it.copy(sortStyle = savedSort) }
        }



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

    fun setSort(newCompareType: Int) = viewModelScope.launch {
        val sortStyle = SortStyle(newCompareType, true)
        _state.update { it.copy(sortStyle = sortStyle) }
        saveSort(sortStyle)
    }

    private suspend fun saveSort(sortStyle: SortStyle) = withContext(Dispatchers.IO) {
        val cacheFile = File(sortCacheFileName)
        cacheFile.writeText(sortStyle.style.toString() + " " + if (sortStyle.ascending) "1" else "0")
    }

    // TODO this should be a usecase and we should be using some kind of
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
