package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.model.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.MediaItem
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import javax.inject.Inject


class ViewMediaItemViewModel @Inject constructor(private val repository: SWMRepository,
                                                 private val connMgr: MyConnectivityManager) : ViewModel() {




    lateinit var liveMediaItem : LiveData<MediaItem>
    lateinit var liveMediaNotes : LiveData<MediaNotes>
    val checkBoxText = repository.getCheckBoxText()

    fun setItemId(itemId: Int){
        liveMediaItem = repository.getLiveMediaItem(itemId)
        liveMediaNotes = repository.getLiveMediaNotes(itemId)
    }

    fun toggleOwned() {
        val notes = liveMediaNotes.value
        notes?.flipOwned()
        repository.update(notes)
    }

    fun toggleWatchedRead() {
        val notes = liveMediaNotes.value
        notes?.flipWatchedRead()
        repository.update(notes)
    }

    fun toggleWantToWatchRead() {
        val notes = liveMediaNotes.value
        notes?.flipWantToWatchRead()
        repository.update(notes)
    }


    fun isNetworkAllowed() = connMgr.isNetworkAllowed()


}