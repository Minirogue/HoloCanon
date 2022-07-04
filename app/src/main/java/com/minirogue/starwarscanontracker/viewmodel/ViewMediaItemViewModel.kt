package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.core.model.PrefsRepo
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaType
import com.minirogue.starwarscanontracker.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewMediaItemViewModel @Inject constructor(
    getCheckboxText: GetCheckboxText,
    private val getMedia: GetMedia,
    private val getNotesForMedia: GetNotesForMedia,
    private val getMediaType: GetMediaType,
    private val updateNotes: UpdateNotes,
    private val connMgr: MyConnectivityManager,
    prefsRepo: PrefsRepo,
) : ViewModel() {

    lateinit var liveMediaItem: LiveData<MediaItem>
    lateinit var liveMediaNotes: LiveData<MediaNotes>
    lateinit var liveMediaType: LiveData<MediaType?>
    val checkBoxText = getCheckboxText()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility

    fun setItemId(itemId: Int) {
        liveMediaItem = getMedia(itemId)
        liveMediaNotes = getNotesForMedia(itemId)
        liveMediaType = Transformations.switchMap(liveMediaItem) { mediaItem: MediaItem? ->
            getMediaType(mediaItem?.type ?: -1)
        }
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

    fun isNetworkAllowed() = connMgr.isNetworkAllowed()
}
