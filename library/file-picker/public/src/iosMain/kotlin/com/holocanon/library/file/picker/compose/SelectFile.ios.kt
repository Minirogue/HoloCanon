package com.holocanon.library.file.picker.compose

@androidx.compose.runtime.Composable
actual fun prepareFilePicker(
    pickerArgs: com.holocanon.library.file.picker.model.PickerArgs,
    onError: (Throwable) -> Unit,
    onCancelled: () -> Unit,
): SelectFileLauncher {
    return SelectFileLauncher {
        TODO("prepareFilePicer() not properly implemented on iOS yet")
    }
}