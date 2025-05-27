package com.minirogue.holocanon.feature.series.internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holocanon.core.model.MediaAndNotes
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.series.model.Series
import com.minirogue.series.usecase.GetSeries
import com.minirogue.series.usecase.GetSeriesIdFromName
import com.minirogue.starwarscanontracker.core.usecase.Checkbox
import com.minirogue.starwarscanontracker.core.usecase.GetMediaAndNotesForSeries
import com.minirogue.starwarscanontracker.core.usecase.IsNetworkAllowed
import com.minirogue.starwarscanontracker.core.usecase.SetCheckboxForSeries
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings

internal data class SeriesState(
    val seriesId: Int? = null,
    val series: Series = Series("", null),
    val mediaAndNotes: List<MediaAndNotes> = emptyList(),
    val isNetworkAllowed: Boolean = false,
    val checkboxSettings: CheckboxSettings? = null,
) {
    val seriesNotes: MediaNotes = MediaNotes(
        mediaAndNotes.all { it.notes.isBox1Checked },
        mediaAndNotes.all { it.notes.isBox2Checked },
        mediaAndNotes.all { it.notes.isBox3Checked },
    )
}

@HiltViewModel(assistedFactory = SeriesViewModel.Factory::class)
internal class SeriesViewModel @AssistedInject constructor(
    @Assisted seriesName: String,
    getSeries: GetSeries,
    getMediaAndNotesForSeries: GetMediaAndNotesForSeries,
    private val setCheckboxForSeries: SetCheckboxForSeries,
    private val getSeriesIdFromName: GetSeriesIdFromName,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(seriesName: String): SeriesViewModel
    }

    private val _state = MutableStateFlow(SeriesState(seriesId = null))
    val state: StateFlow<SeriesState> = _state

    init {
        viewModelScope.launch {
            val seriesId =
                getSeriesIdFromName(seriesName) ?: -1 // TODO this navigation can be improved
            _state.update { it.copy(seriesId = seriesId) }
            getSeries(seriesId)
                .onEach { series -> _state.update { it.copy(series = series) } }
                .launchIn(viewModelScope)
            getMediaAndNotesForSeries(seriesId)
                .onEach { mediaAndNotes -> _state.update { it.copy(mediaAndNotes = mediaAndNotes) } }
                .launchIn(viewModelScope)
        }

        isNetworkAllowed()
            .onEach { shouldAllowNetwork -> _state.update { it.copy(isNetworkAllowed = shouldAllowNetwork) } }
            .launchIn(viewModelScope)
        getCheckboxSettings()
            .onEach { checkboxSettings -> _state.update { it.copy(checkboxSettings = checkboxSettings) } }
            .launchIn(viewModelScope)
    }

    fun toggleCheckbox1(seriesId: Int, newVal: Boolean) = viewModelScope.launch {
        setCheckboxForSeries(Checkbox.CHECKBOX_1, seriesId, newVal)
    }

    fun toggleCheckbox2(seriesId: Int, newVal: Boolean) = viewModelScope.launch {
        setCheckboxForSeries(Checkbox.CHECKBOX_2, seriesId, newVal)
    }

    fun toggleCheckbox3(seriesId: Int, newVal: Boolean) = viewModelScope.launch {
        setCheckboxForSeries(Checkbox.CHECKBOX_3, seriesId, newVal)
    }
}
