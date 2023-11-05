package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.minirogue.api.media.MediaType
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class ViewMediaItemViewModel @Inject constructor(
    getCheckboxText: GetCheckboxText,
    private val getMedia: GetMedia,
    private val getNotesForMedia: GetNotesForMedia,
    private val updateNotes: UpdateNotes,
    connMgr: MyConnectivityManager,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    lateinit var liveMediaItem: LiveData<MediaItem>
    lateinit var liveMediaNotes: LiveData<MediaNotes>
    lateinit var liveMediaTypeDto: LiveData<MediaType?>
    val checkBoxText = getCheckboxText()
    val checkBoxVisibility = getCheckboxSettings().map { checkboxSettings ->
        booleanArrayOf(
            checkboxSettings.checkbox1Setting.isInUse,
            checkboxSettings.checkbox2Setting.isInUse,
            checkboxSettings.checkbox3Setting.isInUse,
        )
    }
    val isNetworkAllowed: Flow<Boolean> = connMgr.isNetworkAllowed()

    fun setItemId(itemId: Int) {
        liveMediaItem = getMedia(itemId)
        liveMediaNotes = getNotesForMedia(itemId)
        liveMediaTypeDto = liveMediaItem.map { MediaType.getFromLegacyId(it.type) }
    }

    fun toggleCheckbox1() {
        val notes = liveMediaNotes.value
        notes?.flipCheck1()
        updateNotes(notes)
    }

    fun toggleCheckbox2() {
        val notes = liveMediaNotes.value
        notes?.flipCheck2()
        updateNotes(notes)
    }

    fun toggleCheckbox3() {
        val notes = liveMediaNotes.value
        notes?.flipCheck3()
        updateNotes(notes)
    }
}
