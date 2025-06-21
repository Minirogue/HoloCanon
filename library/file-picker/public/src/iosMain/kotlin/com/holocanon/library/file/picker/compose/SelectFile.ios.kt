package com.holocanon.library.file.picker.compose

import androidx.compose.runtime.Composable
import com.holocanon.library.file.picker.model.FileType
import com.holocanon.library.file.picker.model.PickerArgs
import kotlinx.io.asSink
import kotlinx.io.asSource
import platform.Foundation.NSURL
import platform.Foundation.NSInputStream
import platform.Foundation.NSOutputStream
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIPresentationController
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.native.concurrent.ThreadLocal


// Implementation derived in large part from https://github.com/Wavesonics/compose-multiplatform-file-picker
@Composable
actual fun prepareFilePicker(
    pickerArgs: PickerArgs,
    onError: (Throwable) -> Unit,
    onCancelled: () -> Unit,
): SelectFileLauncher {
    return SelectFileLauncher {
        val picker = IosFilePicker()
        try {
            // the active launcher references are to keep it in memory due to how IOS handles
            //  memory management.
            IosFilePicker.activeLauncher = picker
            picker.launch(pickerArgs, onCancelled)
        } catch (e: Throwable) {
            onError(e)
        } finally {
            IosFilePicker.activeLauncher = null
        }
    }
}


private class IosFilePicker() {
    fun launch(pickerArgs: PickerArgs, onCancelled: () -> Unit) {
        activeLauncher = this
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(pickerArgs.fileType.toUTType())
        ).apply {
            delegate = PickerDelegate(pickerArgs, onCancelled)
            if (pickerArgs is PickerArgs.Save) {
                pickerArgs.defaultFileName?.let { directoryURL = NSURL(string = it) }
            }
        }
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            // Reusing a closed/dismissed picker causes problems with
            // triggering delegate functions, launch with a new one.
            picker,
            animated = true,
            completion = { picker.allowsMultipleSelection = false },
        )
    }

    @ThreadLocal
    companion object {
        /**
         * For use only with launching plain (no compose dependencies)
         * file picker. When a function completes iOS deallocates
         * unreferenced objects created within it, so we need to
         * keep a reference of the active launcher.
         */
        var activeLauncher: IosFilePicker? = null
    }
}

private fun FileType.toUTType(): UTType? = when (this) {
    FileType.JSON -> UTType.typeWithFilenameExtension(this.mimeType)
}


private class PickerDelegate(
    private val pickerArgs: PickerArgs,
    private val onCancelled: () -> Unit,
) : NSObject(),
    UIDocumentPickerDelegateProtocol,
    UIAdaptivePresentationControllerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        didPickDocumentsAtURLs.onEach { file ->
            (file as? NSURL)?.let { nsUrl ->
                when (pickerArgs) {
                    is PickerArgs.Get -> pickerArgs.onResult(NSInputStream(nsUrl).asSource())
                    is PickerArgs.Save -> pickerArgs.onResult(NSOutputStream(nsUrl, false).asSink())
                }
            }
        }
    }

    override fun documentPickerWasCancelled(
        controller: UIDocumentPickerViewController,
    ) = onCancelled()

    override fun presentationControllerWillDismiss(
        presentationController: UIPresentationController,
    ) {
        (presentationController.presentedViewController as? UIDocumentPickerViewController)
            ?.let { documentPickerWasCancelled(it) }
    }
}
