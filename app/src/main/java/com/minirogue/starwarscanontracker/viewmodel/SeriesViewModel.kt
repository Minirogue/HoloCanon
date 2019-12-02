package com.minirogue.starwarscanontracker.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.starwarscanontracker.model.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SeriesViewModel(private val seriesId: Int) : ViewModel(), KoinComponent {
    private val repository: SWMRepository by inject()
    val liveSeries = repository.getLiveSeries(seriesId)
    val liveSeriesNotes = MediatorLiveData<Array<Boolean>>()
    private val connMgr: ConnectivityManager by inject()
    private val unmeteredOnly: Boolean by inject(named("unmetered_only"))
    private val notesParsingMutex = Mutex()

    init {
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


    private suspend fun updateSeriesNotes(fullNotes: List<MediaNotes>) = withContext(Dispatchers.Default) {
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
