package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.SWMRepository
import org.koin.core.KoinComponent
import org.koin.core.inject


class ViewMediaItemViewModel(application: Application, itemId: Int): AndroidViewModel(application), KoinComponent {

    private val repository : SWMRepository by inject()
    val liveMediaItem = repository.getLiveMediaItem(itemId)
    val liveMediaNotes = repository.getLiveMediaNotes(itemId)
    private val checkboxText = Array(4) {""}
    private val connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val unmeteredOnly: Boolean

    init{
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        checkboxText[1] = prefs.getString(application.getString(R.string.checkbox1_default_text), application.getString(R.string.checkbox1_default_text))?: ""
        checkboxText[2] = prefs.getString(application.getString(R.string.checkbox2_default_text), application.getString(R.string.checkbox2_default_text))?: ""
        checkboxText[3] = prefs.getString(application.getString(R.string.checkbox3_default_text), application.getString(R.string.checkbox3_default_text))?: ""
        unmeteredOnly = prefs.getBoolean(application.getString(R.string.setting_unmetered_sync_only), true)
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

    fun getCheckBoxText(i: Int): String{
        return checkboxText[i]
    }

    fun isNetworkAllowed(): Boolean{
        return !connMgr.isActiveNetworkMetered || !unmeteredOnly
    }

}