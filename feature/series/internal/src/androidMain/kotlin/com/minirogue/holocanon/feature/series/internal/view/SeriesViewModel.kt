package com.minirogue.holocanon.feature.series.internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.series.model.Series
import com.minirogue.series.usecase.GetSeries
import com.minirogue.series.usecase.GetSeriesIdFromName
import com.minirogue.starwarscanontracker.core.model.MediaAndNotes
import com.minirogue.starwarscanontracker.core.usecase.Checkbox
import com.minirogue.starwarscanontracker.core.usecase.GetMediaAndNotesForSeries
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.SetCheckboxForSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetCheckboxText
import javax.inject.Inject

data class SeriesState(
    val seriesId: Int? = null,
    val series: Series = Series("", null),
    val mediaAndNotes: List<MediaAndNotes> = emptyList(),
    val isNetworkAllowed: Boolean = false,
    val checkBoxText: Array<String> = arrayOf("", "", ""),
    val checkBoxVisibility: BooleanArray = booleanArrayOf(false, false, false),
) {
    val seriesNotesArray: List<Boolean> by lazy {
        listOf(
            mediaAndNotes.all { it.notes.isBox1Checked },
            mediaAndNotes.all { it.notes.isBox2Checked },
            mediaAndNotes.all { it.notes.isBox3Checked },
        )
    }
}

@HiltViewModel
internal class SeriesViewModel @Inject constructor(
    private val getSeries: GetSeries,
    getCheckboxText: GetCheckboxText,
    private val getMediaAndNotesForSeries: GetMediaAndNotesForSeries,
    private val setCheckboxForSeries: SetCheckboxForSeries,
    private val getSeriesIdFromName: GetSeriesIdFromName,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {
    private val _state = MutableStateFlow(SeriesState(seriesId = null))
    val state: StateFlow<SeriesState> = _state

    init {
        isNetworkAllowed()
            .onEach { shouldAllowNetwork -> _state.update { it.copy(isNetworkAllowed = shouldAllowNetwork) } }
            .launchIn(viewModelScope)
        getCheckboxText()
            .onEach { checkBoxText -> _state.update { it.copy(checkBoxText = checkBoxText) } }
            .launchIn(viewModelScope)
        getCheckboxSettings().map { checkboxSettings ->
            booleanArrayOf(
                checkboxSettings.checkbox1Setting.isInUse,
                checkboxSettings.checkbox2Setting.isInUse,
                checkboxSettings.checkbox3Setting.isInUse,
            )
        }
            .onEach { checkBoxVisibility -> _state.update { it.copy(checkBoxVisibility = checkBoxVisibility) } }
            .launchIn(viewModelScope)
    }

    fun setSeries(seriesName: String) = viewModelScope.launch {
        val seriesId = getSeriesIdFromName(seriesName) ?: -1 // TODO this navigation can be improved
        _state.update { it.copy(seriesId = seriesId) }
        getSeries(seriesId)
            .onEach { series -> _state.update { it.copy(series = series) } }
            .launchIn(viewModelScope)
        getMediaAndNotesForSeries(seriesId)
            .onEach { mediaAndNotes -> _state.update { it.copy(mediaAndNotes = mediaAndNotes) } }
            .launchIn(viewModelScope)
    }

    fun toggleCheckbox1(newVal: Boolean) = viewModelScope.launch {
        val seriesId = state.value.seriesId
        if (seriesId != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_1, seriesId, newVal)
        }
    }

    fun toggleCheckbox2(newVal: Boolean) = viewModelScope.launch {
        val seriesId = state.value.seriesId
        if (seriesId != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_2, seriesId, newVal)
        }
    }

    fun toggleCheckbox3(newVal: Boolean) = viewModelScope.launch {
        val seriesId = state.value.seriesId
        if (seriesId != null) {
            setCheckboxForSeries(Checkbox.CHECKBOX_3, seriesId, newVal)
        }
    }
}
