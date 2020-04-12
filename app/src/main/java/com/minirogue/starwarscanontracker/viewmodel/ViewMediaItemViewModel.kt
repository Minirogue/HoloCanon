package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.model.PrefsRepo
import com.minirogue.starwarscanontracker.model.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.entity.MediaType
import javax.inject.Inject


class ViewMediaItemViewModel @Inject constructor(private val repository: SWMRepository,
                                                 private val connMgr: MyConnectivityManager,
                                                 prefsRepo: PrefsRepo) : ViewModel() {


    lateinit var liveMediaItem: LiveData<MediaItem>
    lateinit var liveMediaNotes: LiveData<MediaNotes>
    lateinit var liveMediaType: LiveData<MediaType?>
    val checkBoxText = repository.getCheckBoxText()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility

    fun setItemId(itemId: Int) {
        liveMediaItem = repository.getLiveMediaItem(itemId)
        liveMediaNotes = repository.getLiveMediaNotes(itemId)
        liveMediaType = Transformations.switchMap(liveMediaItem) { mediaItem: MediaItem? ->
            repository.getLiveMediaType(mediaItem?.type ?: -1)
        }
    }

    fun toggleCheckbox1() {
        val notes = liveMediaNotes.value
        notes?.flipCheck1()
        repository.update(notes)
    }

    fun toggleCheckbox2() {
        val notes = liveMediaNotes.value
        notes?.flipCheck2()
        repository.update(notes)
    }

    fun toggleCheckbox3() {
        val notes = liveMediaNotes.value
        notes?.flipCheck3()
        repository.update(notes)
    }

    fun isNetworkAllowed() = connMgr.isNetworkAllowed()

}