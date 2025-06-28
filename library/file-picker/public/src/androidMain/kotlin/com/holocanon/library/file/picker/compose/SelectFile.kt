package com.holocanon.library.file.picker.compose

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.holocanon.library.file.picker.model.PickerArgs
import kotlinx.io.asSink
import kotlinx.io.asSource
import java.io.FileNotFoundException

private const val TAG = "SelectFileAndroidImpl"

@Composable
actual fun prepareFilePicker(
    pickerArgs: PickerArgs,
    onError: (Throwable) -> Unit,
    onCancelled: () -> Unit,
): SelectFileLauncher {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val dataUri = result.data?.data
                if (dataUri != null) {
                    try {
                        when (pickerArgs) {
                            is PickerArgs.Get -> {
                                val inputStream = context.contentResolver?.openInputStream(dataUri)
                                if (inputStream != null) {
                                    pickerArgs.onResult(inputStream.asSource())
                                } else {
                                    val throwable = IllegalStateException("null inputStream")
                                    Log.e(
                                        TAG,
                                        "inputStream not found when getting file",
                                        throwable,
                                    )
                                    onError(throwable)
                                }
                            }

                            is PickerArgs.Save -> {
                                val outputStream =
                                    context.contentResolver?.openOutputStream(dataUri)
                                if (outputStream != null) {
                                    pickerArgs.onResult(outputStream.asSink())
                                } else {
                                    val throwable = IllegalStateException("null outputStream")
                                    Log.e(
                                        TAG,
                                        "outputStream not found when saving file",
                                        throwable,
                                    )
                                    onError(throwable)
                                }
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        Log.e(TAG, "Error importing media notes", e)
                        onError(e)
                    }
                }
            } else {
                onCancelled()
            }
        }
    return getSelectFileLauncher(pickerArgs, launcher)
}

private fun getSelectFileLauncher(
    pickerArgs: PickerArgs,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) = when (pickerArgs) {
    is PickerArgs.Get -> SelectFileLauncher {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = pickerArgs.fileType.mimeType
        }
        launcher.launch(intent)
    }

    is PickerArgs.Save -> SelectFileLauncher {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = pickerArgs.fileType.mimeType
            putExtra(Intent.EXTRA_TITLE, pickerArgs.defaultFileName)
        }
        launcher.launch(intent)
    }
}
