package internal.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holocanon.library.media.item.usecase.GetMedia
import com.holocanon.library.settings.usecase.IsNetworkAllowed
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.model.MediaNotes
import com.minirogue.media.notes.usecase.GetNotesForMedia
import com.minirogue.media.notes.usecase.UpdateCheckValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings

internal data class ViewMediaItemState(
    val checkboxSettings: CheckboxSettings? = null,
    val isNetworkAllowed: Boolean = false,
    val mediaItem: StarWarsMedia? = null,
    val mediaNotes: MediaNotes? = null,
)

@Inject
internal class ViewMediaItemViewModel(
    @Assisted itemId: Long,
    getMedia: GetMedia,
    getNotesForMedia: GetNotesForMedia,
    private val updateNotes: UpdateCheckValue,
    isNetworkAllowed: IsNetworkAllowed,
    getCheckboxSettings: GetCheckboxSettings,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(itemId: Long): ViewMediaItemViewModel
    }

    private val _state = MutableStateFlow(ViewMediaItemState())
    val state: StateFlow<ViewMediaItemState> = _state

    init {
        getMedia(itemId)
            .onEach { mediaItem -> _state.update { it.copy(mediaItem = mediaItem) } }
            .launchIn(viewModelScope)
        getNotesForMedia(itemId)
            .onEach { mediaNotes -> _state.update { it.copy(mediaNotes = mediaNotes) } }
            .launchIn(viewModelScope)
        getCheckboxSettings()
            .onEach { checkBoxSettings -> _state.update { it.copy(checkboxSettings = checkBoxSettings) } }
            .launchIn(viewModelScope)
        isNetworkAllowed()
            .onEach { shouldAllowNetwork -> _state.update { it.copy(isNetworkAllowed = shouldAllowNetwork) } }
            .launchIn(viewModelScope)
    }

    fun toggleCheckbox1(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox1, mediaId, newValue)
        }
    }

    fun toggleCheckbox2(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox2, mediaId, newValue)
        }
    }

    fun toggleCheckbox3(newValue: Boolean) = viewModelScope.launch {
        val mediaId = state.value.mediaItem?.id
        if (mediaId != null) {
            updateNotes(CheckBoxNumber.CheckBox3, mediaId, newValue)
        }
    }
}
