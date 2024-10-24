package com.minirogue.holocanon.feature.media.item.internal.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.minirogue.common.model.MediaType
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.usecase.GetMedia
import com.minirogue.starwarscanontracker.core.usecase.GetNotesForMedia
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.UpdateNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxText
import javax.inject.Inject

@HiltViewModel
class ViewMediaItemViewModel @Inject constructor(
    getCheckboxText: GetCheckboxText,
    private val getMedia: GetMedia,
    private val getNotesForMedia: GetNotesForMedia,
    private val updateNotes: UpdateNotes,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    lateinit var liveMediaItemDto: LiveData<MediaItemDto>
    lateinit var liveMediaNotesDto: LiveData<MediaNotesDto>
    lateinit var liveMediaTypeDto: LiveData<MediaType?>
    val checkBoxText = getCheckboxText()
    val checkBoxVisibility = getCheckboxSettings().map { checkboxSettings ->
        booleanArrayOf(
            checkboxSettings.checkbox1Setting.isInUse,
            checkboxSettings.checkbox2Setting.isInUse,
            checkboxSettings.checkbox3Setting.isInUse,
        )
    }
    val isNetworkAllowed: Flow<Boolean> = isNetworkAllowed()

    fun setItemId(itemId: Int) {
        liveMediaItemDto = getMedia(itemId)
        liveMediaNotesDto = getNotesForMedia(itemId).asLiveData(viewModelScope.coroutineContext)
        liveMediaTypeDto = liveMediaItemDto.map { MediaType.getFromLegacyId(it.type) }
    }

    fun toggleCheckbox1() = viewModelScope.launch {
        val notes = liveMediaNotesDto.value
        notes?.flipCheck1()
        updateNotes(notes)
    }

    fun toggleCheckbox2() = viewModelScope.launch {
        val notes = liveMediaNotesDto.value
        notes?.flipCheck2()
        updateNotes(notes)
    }

    fun toggleCheckbox3() = viewModelScope.launch {
        val notes = liveMediaNotesDto.value
        notes?.flipCheck3()
        updateNotes(notes)
    }
}
