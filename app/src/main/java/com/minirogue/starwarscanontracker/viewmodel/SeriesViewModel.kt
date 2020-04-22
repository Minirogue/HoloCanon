package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.model.PrefsRepo
import com.minirogue.starwarscanontracker.model.repository.SWMRepository
import com.minirogue.starwarscanontracker.model.room.entity.FilterObject
import com.minirogue.starwarscanontracker.model.room.entity.FilterType
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.model.room.entity.Series
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SeriesViewModel @Inject constructor(private val repository: SWMRepository,
                                          prefsRepo: PrefsRepo,
                                          private val connMgr: MyConnectivityManager) : ViewModel() {

    private var seriesId: Int = -1
    lateinit var liveSeries: LiveData<Series>
    val seriesList = MediatorLiveData<List<MediaAndNotes>>()
    val liveSeriesNotes = MediatorLiveData<Array<Boolean>>()
    val checkBoxNames = repository.getCheckBoxText()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility
    private val notesParsingMutex = Mutex()


    fun setSeriesId(seriesId: Int) {
        this.seriesId = seriesId
        liveSeries = repository.getLiveSeries(seriesId)
        liveSeriesNotes.addSource(repository.getLiveNotesBySeries(seriesId)) {
            viewModelScope.launch {
                updateSeriesNotes(it)
            }
        }
        viewModelScope.launch {
            seriesList.addSource(repository.getMediaListWithNotes(listOf(FilterObject(seriesId, FilterType.FILTERCOLUMN_SERIES, true, "series filter")))) { mediaAndNotesList -> seriesList.postValue(mediaAndNotesList) }
        }
    }

    fun toggleCheckbox1() {
        val oldVal = liveSeriesNotes.value?.get(0)
        if (oldVal != null) {
            repository.setSeriesCheckbox1(seriesId, !oldVal)
        }
    }

    fun toggleCheckbox2() {
        val oldVal = liveSeriesNotes.value?.get(1)
        if (oldVal != null) {
            repository.setSeriesCheckbox2(seriesId, !oldVal)
        }
    }

    fun toggleCheckbox3() {
        val oldVal = liveSeriesNotes.value?.get(2)
        if (oldVal != null) {
            repository.setSeriesCheckbox3(seriesId, !oldVal)
        }
    }

    private suspend fun updateSeriesNotes(fullNotes: List<MediaNotes>) = withContext(Dispatchers.Default) {
        notesParsingMutex.withLock {
            val checkBoxOne = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isBox1Checked) {
                        checked = false
                        break
                    }
                }
                checked
            }
            val checkBoxTwo = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isBox2Checked) {
                        checked = false
                        break
                    }
                }
                checked
            }
            val checkBoxThree = async {
                var checked = true
                for (notes in fullNotes) {
                    if (!notes.isBox3Checked) {
                        checked = false
                        break
                    }
                }
                checked
            }
            liveSeriesNotes.postValue(arrayOf(checkBoxOne.await(), checkBoxTwo.await(), checkBoxThree.await()))
        }
    }

    fun isNetworkAllowed() = connMgr.isNetworkAllowed()

}
