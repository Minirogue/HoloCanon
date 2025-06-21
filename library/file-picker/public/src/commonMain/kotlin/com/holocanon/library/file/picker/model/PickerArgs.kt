package com.holocanon.library.file.picker.model

import kotlinx.io.RawSink
import kotlinx.io.RawSource

sealed class PickerArgs {
    abstract val fileType: FileType
    data class Get(override val fileType: FileType, val onResult: (RawSource) -> Unit) : PickerArgs()
    data class Save(
        override val fileType: FileType,
        val defaultFileName: String? = null,
        val onResult: (RawSink) -> Unit,
    ) : PickerArgs()
}
