package com.minirogue.starwarscanontracker.viewmodel

import androidx.lifecycle.*
import com.minirogue.starwarscanontracker.application.MyConnectivityManager
import com.minirogue.starwarscanontracker.core.model.PrefsRepo
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.Series
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import com.minirogue.starwarscanontracker.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val getSeries: GetSeries,
    getCheckboxText: GetCheckboxText,
    private val getNotesBySeries: GetNotesBySeries,
    private val getMediaAndNotesForSeries: GetMediaAndNotesForSeries,
    private val setCheckboxForSeries: SetCheckboxForSeries,
    prefsRepo: PrefsRepo,
    private val connMgr: MyConnectivityManager,
) : ViewModel() {

    private var seriesId: Int = -1
    lateinit var liveSeries: LiveData<Series>
    val seriesList = MediatorLiveData<List<MediaAndNotes>>()
    val liveSeriesNotes = MediatorLiveData<Array<Boolean>>()
    val checkBoxNames = getCheckboxText.invoke()
    val checkBoxVisibility = prefsRepo.checkBoxVisibility
    private val notesParsingMutex = Mutex()

    fun setSeriesId(seriesId: Int) {
        this.seriesId = seriesId
        liveSeries = getSeries(seriesId).asLiveData(viewModelScope.coroutineContext)
        liveSeriesNotes.addSource(getNotesBySeries(seriesId)) {
            viewModelScope.launch {
                updateSeriesNotes(it)
            }
        }
        viewModelScope.launch {
            seriesList.addSource(getMediaAndNotesForSeries(seriesId).asLiveData()) { mediaAndNotesList ->
                seriesList.postValue(mediaAndNotesList)
            }
        }
    }

    fun toggleCheckbox1() {
        val oldVal = liveSeriesNotes.value?.get(0)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_1, seriesId, !oldVal)
        }
    }

    fun toggleCheckbox2() {
        val oldVal = liveSeriesNotes.value?.get(1)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_2, seriesId, !oldVal)
        }
    }

    fun toggleCheckbox3() {
        val oldVal = liveSeriesNotes.value?.get(2)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_3, seriesId, !oldVal)
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
