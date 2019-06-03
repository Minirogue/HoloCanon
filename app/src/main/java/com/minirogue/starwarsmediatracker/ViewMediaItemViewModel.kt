package com.minirogue.starwarsmediatracker

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import com.minirogue.starwarsmediatracker.database.SWMRepository


class ViewMediaItemViewModel(application: Application, val itemId: Int): AndroidViewModel(application) {

    private val repository = SWMRepository(application)
    val liveMediaItem = repository.getLiveMediaItem(itemId)
    val liveMediaNotes = repository.getLiveMediaNotes(itemId)
    val checkboxText = Array(4) {""}

    init{
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        checkboxText[1] = prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read))?: ""
        checkboxText[2] = prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read))?: ""
        checkboxText[3] = prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned))?: ""
    }


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
    fun getCheckBoxText(i: Int): String{
        return checkboxText[i]
    }

}