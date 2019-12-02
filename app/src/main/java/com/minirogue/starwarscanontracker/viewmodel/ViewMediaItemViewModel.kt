package com.minirogue.starwarscanontracker.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import com.minirogue.starwarscanontracker.model.SWMRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named


class ViewMediaItemViewModel(itemId: Int): ViewModel(), KoinComponent {

    private val repository : SWMRepository by inject()
    val liveMediaItem = repository.getLiveMediaItem(itemId)
    val liveMediaNotes = repository.getLiveMediaNotes(itemId)
    val checkBoxText = repository.getCheckBoxText()
    private val connMgr: ConnectivityManager by inject()
    private val unmeteredOnly: Boolean by inject(named("unmetered_only"))



    fun toggleOwned(){
        val notes = liveMediaNotes.value
        notes?.flipOwned()
        repository.update(notes)
    }
    fun toggleWatchedRead(){
        val notes = liveMediaNotes.value
        notes?.flipWatchedRead()
        repository.update(notes)
    }
    fun toggleWantToWatchRead(){
        val notes = liveMediaNotes.value
        notes?.flipWantToWatchRead()
        repository.update(notes)
    }


    fun isNetworkAllowed(): Boolean{
        return !connMgr.isActiveNetworkMetered || !unmeteredOnly
    }

}