package com.minirogue.holocanon.feature.media.list.internal.viewmodel

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holocanon.core.model.MediaAndNotes
import com.holocanon.library.media.item.usecase.GetMediaListWithNotes
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import com.holocanon.library.sorting.model.SortStyle
import com.holocanon.library.sorting.usecase.GetSortStyle
import com.holocanon.library.sorting.usecase.ReverseSort
import com.holocanon.library.sorting.usecase.SaveSortStyle
import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.usecase.UpdateCheckValue
import dagger.hilt.android.lifecycle.HiltViewModel
import filters.GetActiveFilters
import filters.UpdateFilter
import filters.model.MediaFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

@Immutable // TODO benchmark with/without this
internal data class MediaListState(
    val activeFilters: List<MediaFilter> = emptyList(),
    val sortStyle: SortStyle = SortStyle.DEFAULT_STYLE,
    val scrollPosition: Int = 0,
    val scrollOffset: Int = 0,
    val searchTerm: String? = null,
    val checkboxSettings: CheckboxSettings? = null,
    val isNetworkAllowed: Boolean = false,
)

@HiltViewModel
internal class MediaListViewModel @Inject constructor(
    getActiveFilters: GetActiveFilters,
    private val updateFilter: UpdateFilter,
    private val updateCheckValue: UpdateCheckValue,
    getMediaListWithNotes: GetMediaListWithNotes,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
    getSortStyle: GetSortStyle,
    private val saveSortStyle: SaveSortStyle,
    private val reverseSort: ReverseSort,
) : ViewModel() {
    private data class BackingState(
        val scrollPosition: Int = 0,
        val scrollOffset: Int = 0,
        val searchTerm: String? = null,
    )

    private val backingState: MutableStateFlow<BackingState> = MutableStateFlow(BackingState())

    val state: Flow<MediaListState> = combine(
        backingState,
        getActiveFilters(),
        getCheckboxSettings(),
        isNetworkAllowed(),
        getSortStyle.getSortStyle(),
    ) { backing, activeFilters, checkboxSettings, isNetworkAllowed, sortStyle ->
        MediaListState(
            activeFilters = activeFilters,
            sortStyle = sortStyle,
            scrollPosition = backing.scrollPosition,
            scrollOffset = backing.scrollOffset,
            searchTerm = backing.searchTerm,
            checkboxSettings = checkboxSettings,
            isNetworkAllowed = isNetworkAllowed,
        )
    }

    // TODO benchmark and compare with possible @Immutable usage
    val mediaList: Flow<List<MediaAndNotes>> = getMediaListWithNotes()
        .combine(state) { list, state -> performSort(list, state.sortStyle, state.searchTerm) }

    fun updateSearch(searchTerm: String) {
        backingState.update { it.copy(searchTerm = searchTerm) }
    }

    fun setSort(newCompareType: Int) = viewModelScope.launch {
        val sortStyle = SortStyle(newCompareType, true)
        saveSortStyle.saveSortStyle(sortStyle)
    }

    fun onScroll(index: Int, offset: Int) {
        backingState.update { it.copy(scrollPosition = index, scrollOffset = offset) }
    }

    private suspend fun performSort(
        list: List<MediaAndNotes>,
        sortStyle: SortStyle,
        searchTerm: String?,
    ): List<MediaAndNotes> = withContext(Dispatchers.Default) {
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

    fun reverseSort() = viewModelScope.launch {
        reverseSort.reverse()
    }

    private suspend fun List<MediaAndNotes>.sort(sortStyle: SortStyle?): List<MediaAndNotes> =
        withContext(Dispatchers.Default) {
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
