package com.minirogue.holocanon.feature.series.internal.view

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.minirogue.series.model.Series
import com.minirogue.series.usecase.GetSeries
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import com.minirogue.starwarscanontracker.core.usecase.Checkbox
import com.minirogue.starwarscanontracker.core.usecase.GetMediaAndNotesForSeries
import com.minirogue.starwarscanontracker.core.usecase.GetNotesBySeries
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.SetCheckboxForSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxText
import javax.inject.Inject

@Suppress("LongParameterList")
@HiltViewModel
internal class SeriesViewModel @Inject constructor(
    private val getSeries: GetSeries,
    getCheckboxText: GetCheckboxText,
    private val getNotesBySeries: GetNotesBySeries,
    private val getMediaAndNotesForSeries: GetMediaAndNotesForSeries,
    private val setCheckboxForSeries: SetCheckboxForSeries,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    private var seriesId: Int = -1
    lateinit var seriesFlow: Flow<Series>
    val seriesList = MediatorLiveData<List<MediaAndNotes>>()
    val liveSeriesNotes = MediatorLiveData<Array<Boolean>>()
    val checkBoxNames = getCheckboxText.invoke()
    val checkBoxVisibility = getCheckboxSettings().map { checkboxSettings ->
        booleanArrayOf(
            checkboxSettings.checkbox1Setting.isInUse,
            checkboxSettings.checkbox2Setting.isInUse,
            checkboxSettings.checkbox3Setting.isInUse,
        )
    }
    val isNetworkAllowed = isNetworkAllowed()

    private val notesParsingMutex = Mutex()

    fun setSeriesId(seriesId: Int) {
        this.seriesId = seriesId
        seriesFlow = getSeries(seriesId)
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

    fun toggleCheckbox1() = viewModelScope.launch {
        val oldVal = liveSeriesNotes.value?.get(0)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_1, seriesId, !oldVal)
        }
    }

    fun toggleCheckbox2() = viewModelScope.launch {
        val oldVal = liveSeriesNotes.value?.get(1)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_2, seriesId, !oldVal)
        }
    }

    fun toggleCheckbox3() = viewModelScope.launch {
        val oldVal = liveSeriesNotes.value?.get(2)
        if (oldVal != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_3, seriesId, !oldVal)
        }
    }

    private suspend fun updateSeriesNotes(fullNotes: List<MediaNotesDto>) =
        withContext(Dispatchers.Default) {
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
                liveSeriesNotes.postValue(
                    arrayOf(
                        checkBoxOne.await(),
                        checkBoxTwo.await(),
                        checkBoxThree.await()
                    )
                )
            }
        }
}
