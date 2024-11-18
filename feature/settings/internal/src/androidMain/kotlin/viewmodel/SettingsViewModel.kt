package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.common.model.MediaType
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.media.notes.ExportMediaNotesJson
import com.minirogue.media.notes.ImportMediaNotesJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetPermanentFilterSettings
import settings.usecase.ShouldSyncViaWifiOnly
import settings.usecase.FlipIsCheckboxActive
import settings.usecase.UpdateCheckboxName
import settings.usecase.UpdatePermanentFilterSettings
import settings.usecase.UpdateWifiSetting
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

internal data class SettingsState(
    val checkboxSettings: CheckboxSettings? = null,
    val nameChangeDialogShowing: Int? = null,
    val permanentFilters: Map<MediaType, Boolean>? = null,
    val wifiOnly: Boolean? = null,
)

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val getCheckboxSettings: GetCheckboxSettings,
    private val updateCheckboxName: UpdateCheckboxName,
    private val updateCheckboxActive: FlipIsCheckboxActive,
    private val getPermanentFilterSettings: GetPermanentFilterSettings,
    private val updatePermanentFilterSettings: UpdatePermanentFilterSettings,
    private val maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase,
    private val updateWifiSetting: UpdateWifiSetting,
    private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
    private val exportMediaNotesJson: ExportMediaNotesJson,
    private val importMediaNotesJson: ImportMediaNotesJson,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            getCheckboxSettings().collect { newCheckboxSettings ->
                _state.update {
                    it.copy(
                        checkboxSettings = newCheckboxSettings
                    )
                }
            }
        }
        viewModelScope.launch {
            getPermanentFilterSettings().collect { newPermFilters ->
                _state.update {
                    it.copy(
                        permanentFilters = newPermFilters
                    )
                }
            }
        }
        viewModelScope.launch {
            shouldSyncViaWifiOnly().collect { wifiOnly -> _state.update { it.copy(wifiOnly = wifiOnly) } }
        }
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

    fun importMediaNotes(inputStream: InputStream)  {
        importMediaNotesJson(inputStream)
    }

    fun exportMediaNotes(outputStream: OutputStream)  {
        exportMediaNotesJson(outputStream)
    }
}
