package com.holocanon.library.file.picker.model

import kotlinx.io.RawSink
import kotlinx.io.RawSource

sealed class PickerArgs {
    data class Get(val fileType: FileType, val onResult: (RawSource) -> Unit) : PickerArgs()
    data class Save(
        val fileType: FileType,
        val defaultFileName: String? = null,
        val onResult: (RawSink) -> Unit,
    ) : PickerArgs()
}
