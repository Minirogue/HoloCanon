package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minirogue.api.media.MediaType
import com.minirogue.usecase.UpdateMediaDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import settings.usecase.GetPermanentFilterSettings
import settings.usecase.ShouldSyncViaWifiOnly
import settings.usecase.UpdateCheckboxActive
import settings.usecase.UpdateCheckboxName
import settings.usecase.UpdatePermanentFilterSettings
import settings.usecase.UpdateWifiSetting
import javax.inject.Inject

internal data class SettingsState(
        val checkboxSettings: CheckboxSettings? = null,
        val nameChangeDialogShowing: Int? = null,
        val permanentFilters: Map<MediaType, Boolean>? = null,
        val wifiOnly: Boolean? = null,
)

@Suppress("LongParameterList")
@HiltViewModel
internal class SettingsViewModel @Inject constructor(
        private val getCheckboxSettings: GetCheckboxSettings,
        private val updateCheckboxName: UpdateCheckboxName,
        private val updateCheckboxActive: UpdateCheckboxActive,
        private val getPermanentFilterSettings: GetPermanentFilterSettings,
        private val updatePermanentFilterSettings: UpdatePermanentFilterSettings,
        private val updateMediaDatabase: UpdateMediaDatabaseUseCase,
        private val updateWifiSetting: UpdateWifiSetting,
        private val shouldSyncViaWifiOnly: ShouldSyncViaWifiOnly,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            getCheckboxSettings().collect { newCheckboxSettings -> _state.update { it.copy(checkboxSettings = newCheckboxSettings) } }
        }
        viewModelScope.launch {
            getPermanentFilterSettings().collect { newPermFilters -> _state.update { it.copy(permanentFilters = newPermFilters) } }
        }
        viewModelScope.launch {
            shouldSyncViaWifiOnly().collect { wifiOnly -> _state.update { it.copy(wifiOnly = wifiOnly) } }
        }
    }

    fun setCheckboxActive(whichBox: Int, newValue: Boolean) = viewModelScope.launch {
        updateCheckboxActive(whichBox, newValue)
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

    fun setPermanentFilterActive(mediaType: MediaType, newActiveValue: Boolean) = viewModelScope.launch {
        updatePermanentFilterSettings(mediaType, newActiveValue)
    }

    fun toggleWifiSetting(newValue: Boolean) = viewModelScope.launch {
        updateWifiSetting(newValue)
    }

    fun syncDatabase() = updateMediaDatabase(true)
}
