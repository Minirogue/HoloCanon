package com.holocanon.feature.settings.internal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.common.model.MediaType
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.media.notes.ExportMediaNotesJson
import com.minirogue.media.notes.ImportMediaNotesJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.model.DarkModeSetting
import settings.model.Theme
import settings.usecase.FlipIsCheckboxActive
import settings.usecase.GetAllSettings
import settings.usecase.UpdateCheckboxName
import settings.usecase.UpdateDarkModeSetting
import settings.usecase.UpdatePermanentFilterSettings
import settings.usecase.UpdateTheme
import settings.usecase.UpdateWifiSetting
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

internal data class SettingsState(
    val checkboxSettings: CheckboxSettings? = null,
    val nameChangeDialogShowing: Int? = null,
    val permanentFilters: Map<MediaType, Boolean>? = null,
    val wifiOnly: Boolean? = null,
    val theme: Theme? = null,
    val darkModeSetting: DarkModeSetting? = null,
)

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    getAllSettings: GetAllSettings,
    private val updateCheckboxName: UpdateCheckboxName,
    private val updateCheckboxActive: FlipIsCheckboxActive,
    private val updatePermanentFilterSettings: UpdatePermanentFilterSettings,
    private val maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase,
    private val updateWifiSetting: UpdateWifiSetting,
    private val updateTheme: UpdateTheme,
    private val updateDarkModeSetting: UpdateDarkModeSetting,
    private val exportMediaNotesJson: ExportMediaNotesJson,
    private val importMediaNotesJson: ImportMediaNotesJson,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        getAllSettings()
            .onEach { settings ->
                _state.update {
                    it.copy(
                        checkboxSettings = settings.checkboxSettings,
                        permanentFilters = settings.permanentFilterSettings,
                        wifiOnly = settings.syncWifiOnly,
                        theme = settings.theme,
                        darkModeSetting = settings.darkModeSetting,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateTheme(newTheme: Theme) = viewModelScope.launch {
        updateTheme.invoke(newTheme)
    }

    fun updateDarkModeSetting(newDarkModeSetting: DarkModeSetting) = viewModelScope.launch {
        updateDarkModeSetting.invoke(newDarkModeSetting)
    }

    fun flipIsCheckboxActive(whichBox: Int) = viewModelScope.launch {
        updateCheckboxActive(whichBox)
    }

    fun setCheckboxName(whichBox: Int, newName: String) = viewModelScope.launch {
        dismissNameChangeDialog()
        updateCheckboxName(whichBox, newName)
    }

    fun showNameChangeDialog(whichBox: Int) {
        _state.update { it.copy(nameChangeDialogShowing = whichBox) }
    }

    fun dismissNameChangeDialog() {
        _state.update { it.copy(nameChangeDialogShowing = null) }
    }

    fun setPermanentFilterActive(mediaType: MediaType, newActiveValue: Boolean) =
        viewModelScope.launch {
            updatePermanentFilterSettings(mediaType, newActiveValue)
        }

    fun toggleWifiSetting(newValue: Boolean) = viewModelScope.launch {
        updateWifiSetting(newValue)
    }

    fun syncDatabase() = viewModelScope.launch { maybeUpdateMediaDatabase(true) }

    fun importMediaNotes(inputStream: InputStream) = viewModelScope.launch {
        importMediaNotesJson(inputStream)
    }

    fun exportMediaNotes(outputStream: OutputStream) = viewModelScope.launch {
        exportMediaNotesJson(outputStream)
    }
}
