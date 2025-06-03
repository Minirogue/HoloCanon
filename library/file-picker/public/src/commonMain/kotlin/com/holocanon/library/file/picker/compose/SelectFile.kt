package com.holocanon.library.file.picker.compose

import androidx.compose.runtime.Composable
import com.holocanon.library.file.picker.model.PickerArgs

@Composable
expect fun prepareFilePicker(
    pickerArgs: PickerArgs,
    onError: (Throwable) -> Unit,
    onCancelled: () -> Unit,
): SelectFileLauncher

fun interface SelectFileLauncher {
    fun launch()
}
