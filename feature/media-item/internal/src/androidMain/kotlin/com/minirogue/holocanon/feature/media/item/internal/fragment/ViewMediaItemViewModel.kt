package com.minirogue.holocanon.feature.media.item.internal.fragment

import android.media.browse.MediaBrowser
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.holocanon.library.media.item.usecase.GetMedia
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.CheckBoxNumber
import com.minirogue.media.notes.UpdateCheckValue
import com.minirogue.starwarscanontracker.core.model.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.usecase.GetNotesForMedia
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.UpdateNotes
import kotlinx.coroutines.flow.MutableStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxText
import javax.inject.Inject

data class ViewMediaItemState(
    val checkboxtext: Array<String> = arrayOf("", "", ""),
    val checkboxVisibility: BooleanArray = booleanArrayOf(false, false, false),
    val isNetworkAllowed: Boolean = false,
    val mediaItem: StarWarsMedia? = null,
    val mediaNotes: MediaNotes? = null,
)

@HiltViewModel
class ViewMediaItemViewModel @Inject constructor(
    getCheckboxText: GetCheckboxText,
    private val getMedia: GetMedia,
    private val getNotesForMedia: GetNotesForMedia,
    private val updateNotes: UpdateCheckValue,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    val state: StateFlow<ViewMediaItemState>
        private field = MutableStateFlow(ViewMediaItemState())

    init {
        getCheckboxText()
            .onEach { checkboxText -> state.update { it.copy(checkboxText = checkboxText) } }
            .launchIn(viewModelScope)
        getCheckboxSettings().map { checkboxSettings ->
            booleanArrayOf(
                checkboxSettings.checkbox1Setting.isInUse,
                checkboxSettings.checkbox2Setting.isInUse,
                checkboxSettings.checkbox3Setting.isInUse,
            )
        }
            .onEach { checkBoxVisibility -> state.update { it.copy(checkboxVisibility = checkBoxVisibility) } }
            .launchIn(viewModelScope)
        isNetworkAllowed()
            .onEach { shouldAllowNetwork -> state.update { it.copy(isNetworkAllowed = shouldAllowNetwork) } }
            .launchIn(viewModelScope)
    }

    fun setItemId(itemId: Int) {
        getMedia(itemId)
            .onEach { mediaItem -> state.update { it.copy(mediaItem = mediaItem) } }
            .launchIn(viewModelScope)
        getNotesForMedia(itemId)
            .onEach { mediaNotes -> state.update { it.copy(mediaNotes = mediaNotes) } }
            .launchIn(viewModelScope)
    }

    fun toggleCheckbox1(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox1, mediaId, newValue)
        }
    }

    fun toggleCheckbox2(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox2, mediaId, newValue)
        }
    }

    fun toggleCheckbox3(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox3, mediaId, newValue)
        }
    }
}
