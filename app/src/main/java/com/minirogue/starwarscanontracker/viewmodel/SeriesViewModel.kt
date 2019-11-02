package com.minirogue.starwarscanontracker.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.minirogue.starwarscanontracker.R
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.SWMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class SeriesViewModel(application: Application, val seriesId: Int) : AndroidViewModel(application), KoinComponent {
    private val repository: SWMRepository by inject()
    val liveSeries = repository.getLiveSeries(seriesId)
    val liveSeriesNotes = MediatorLiveData<Array<Boolean>>()
    private val connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val unmeteredOnly: Boolean
    private val notesParsingMutex = Mutex()

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        unmeteredOnly = prefs.getBoolean(application.getString(R.string.setting_unmetered_sync_only), true)
        liveSeriesNotes.addSource(repository.getLiveNotesBySeries(seriesId)) {
            viewModelScope.launch {
                updateSeriesNotes(it)
            }
        }
    }

    fun toggleWatchedRead() {
        val oldVal = liveSeriesNotes.value?.get(0)
        if (oldVal != null) {
            repository.setSeriesWatchedRead(seriesId, !oldVal)
        }
    }

    fun toggleWantToWatchRead() {
        val oldVal = liveSeriesNotes.value?.get(1)
        if (oldVal != null) {
            repository.setSeriesWantToWatchRead(seriesId, !oldVal)
        }
    }

    fun toggleOwned() {
        val oldVal = liveSeriesNotes.value?.get(2)
        if (oldVal != null) {
            repository.setSeriesOwned(seriesId, !oldVal)
        }
    }


    suspend fun updateSeriesNotes(fullNotes: List<MediaNotes>) = withContext(Dispatchers.Default) {
        notesParsingMutex.withLock {
            val checkBoxOne = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isWatchedRead) {
                        checked = false
                        break
                    }
                }
                checked
            }
            val checkBoxTwo = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isWantToWatchRead) {
                        checked = false
                        break
                    }
                }
                checked
            }
            val checkBoxThree = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isOwned) {
                        checked = false
                        break
                    }
                }
                checked
            }
            liveSeriesNotes.postValue(arrayOf(checkBoxOne.await(), checkBoxTwo.await(), checkBoxThree.await()))
        }
    }

    fun isNetworkAllowed(): Boolean {
        return !connMgr.isActiveNetworkMetered || !unmeteredOnly
    }

}
