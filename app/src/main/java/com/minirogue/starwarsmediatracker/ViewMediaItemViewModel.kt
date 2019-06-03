package com.minirogue.starwarsmediatracker

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import com.minirogue.starwarsmediatracker.database.SWMRepository


class ViewMediaItemViewModel(application: Application, val itemId: Int): AndroidViewModel(application) {

    private val repository = SWMRepository(application)
    val liveMediaItem = repository.getLiveMediaItem(itemId)
    val liveMediaNotes = repository.getLiveMediaNotes(itemId)


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
    fun getCoverImageFromURL(url: String): Drawable {
        return repository.getCoverImageFromURL(url)
    }

}