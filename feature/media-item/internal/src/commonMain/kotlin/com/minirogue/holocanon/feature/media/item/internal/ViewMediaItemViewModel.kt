package com.minirogue.holocanon.feature.media.item.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holocanon.library.media.item.usecase.GetMedia
import com.holocanon.library.settings.usecase.GetCheckboxSettings
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.media.notes.usecase.GetNotesForMedia
import com.minirogue.media.notes.usecase.UpdateCheckValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings

internal data class ViewMediaItemState(
    val checkboxSettings: CheckboxSettings,
    val isNetworkAllowed: Boolean,
    val mediaItem: StarWarsMedia,
    val mediaNotes: MediaNotes,
)

@Inject
internal class ViewMediaItemViewModel(
    @Assisted itemId: Long,
    getMedia: GetMedia,
    getNotesForMedia: GetNotesForMedia,
    private val updateNotes: UpdateCheckValue,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(itemId: Long): ViewMediaItemViewModel
    }

    val state = combine(
        getMedia(itemId),
        getNotesForMedia(itemId),
        getCheckboxSettings(),
        isNetworkAllowed(),
    ) { mediaItem, mediaNotes, checkboxSettings, networkAllowed ->
        ViewMediaItemState(
            checkboxSettings = checkboxSettings,
            isNetworkAllowed = networkAllowed,
            mediaItem = mediaItem,
            mediaNotes = mediaNotes,
        )
    }

    fun toggleCheckbox1(mediaId: Long, newValue: Boolean) = viewModelScope.launch {
        updateNotes(CheckBoxNumber.CheckBox1, mediaId, newValue)
    }

    fun toggleCheckbox2(mediaId: Long, newValue: Boolean) = viewModelScope.launch {
        updateNotes(CheckBoxNumber.CheckBox2, mediaId, newValue)
    }

    fun toggleCheckbox3(mediaId: Long, newValue: Boolean) = viewModelScope.launch {
        updateNotes(CheckBoxNumber.CheckBox3, mediaId, newValue)
    }
}
