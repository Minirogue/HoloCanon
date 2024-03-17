package com.minirogue.holocanon.feature.media.list.internal.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.media.notes.CheckBoxNumber
import com.minirogue.media.notes.UpdateCheckValue
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.SortStyle
import com.minirogue.starwarscanontracker.core.usecase.GetMediaListWithNotes
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.GetActiveFilters
import filters.UpdateFilter
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import java.io.File
import javax.inject.Inject

internal data class MediaListState(
        val activeFilters: List<MediaFilter> = emptyList(),
        val sortStyle: SortStyle = SortStyle.DEFAULT_STYLE,
        val searchTerm: String? = null,
        val checkboxSettings: CheckboxSettings = CheckboxSettings.NONE,
        val isNetworkAllowed: Boolean = false,
)

@Suppress("LongParameterList")
@HiltViewModel
internal class MediaListViewModel @Inject constructor(
        getActiveFilters: GetActiveFilters,
        private val updateFilter: UpdateFilter,
        private val updateCheckValue: UpdateCheckValue,
        getMediaListWithNotes: GetMediaListWithNotes,
        isNetworkAllowed: IsNetworkAllowed,
        getCheckboxSettings: GetCheckboxSettings,
        application: Application,
) : ViewModel() {

    private val _state: MutableStateFlow<MediaListState> = MutableStateFlow(MediaListState())
    val state: StateFlow<MediaListState> = _state
    val mediaList: Flow<List<MediaAndNotes>> = getMediaListWithNotes()
            .combine(_state) { list, state ->
                withContext(Dispatchers.Default) {
                    val sortStyle = state.sortStyle
                    val searchTerm = state.searchTerm
                    if (!searchTerm.isNullOrBlank()) {
                        list.filter {
                            it.mediaItem.title.contains(searchTerm, true) ||
                                    it.mediaItem.description?.contains(searchTerm, true) == true ||
                                    it.mediaItem.series?.contains(searchTerm, true) == true
                        }
                    } else {
                        list
                    }.sort(sortStyle)
                }
            }

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
    }

    fun updateSearch(searchTerm: String?) {
        _state.update { it.copy(searchTerm = searchTerm) }
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
            SortStyle.DEFAULT_STYLE
        } else {
            val split = cacheFile.readText().split(" ")
            SortStyle(split[0].toInt(), split[1].toInt() == 1)
        }
    }

    fun reverseSort() {
        _state.update { it.copy(sortStyle = it.sortStyle.reversed()) }
    }

    private suspend fun List<MediaAndNotes>.sort(sortStyle: SortStyle?): List<MediaAndNotes> = withContext(Dispatchers.Default) {
        this@sort.sortedWith(sortStyle ?: SortStyle.DEFAULT_STYLE)
    }

    fun onCheckBox1Clicked(itemId: Long, newValue: Boolean) = viewModelScope.launch {
        updateCheckValue(CheckBoxNumber.CheckBox1, itemId, newValue)
    }

    fun onCheckBox2Clicked(itemId: Long, newValue: Boolean) = viewModelScope.launch {
        updateCheckValue(CheckBoxNumber.CheckBox2, itemId, newValue)
    }

    fun onCheckBox3Clicked(itemId: Long, newValue: Boolean) = viewModelScope.launch {
        updateCheckValue(CheckBoxNumber.CheckBox3, itemId, newValue)
    }

    fun deactivateFilter(mediaFilter: MediaFilter) = viewModelScope.launch {
        val newMediaFilter = mediaFilter.copy(isActive = false)
        updateFilter(newMediaFilter)
    }
}
