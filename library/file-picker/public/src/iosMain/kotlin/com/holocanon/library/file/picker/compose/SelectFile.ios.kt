package com.holocanon.library.file.picker.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import platform.UIKit.UIDocumentPickerMode
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.native.concurrent.ThreadLocal


// Implementation derived in large part from https://github.com/Wavesonics/compose-multiplatform-file-picker
//  and https://medium.com/@adman.shadman/building-a-cross-platform-document-and-gallery-file-picker-in-kotlin-multiplatform-73e2c38db092
@Composable
actual fun prepareFilePicker(
    pickerArgs: PickerArgs,
    onError: (Throwable) -> Unit,
    onCancelled: () -> Unit,
): SelectFileLauncher {
    val picker = when(pickerArgs){
        is PickerArgs.Get -> UIDocumentPickerViewController(
            forOpeningContentTypes =  listOf(pickerArgs.fileType.toUTType()),
        )
        is PickerArgs.Save -> UIDocumentPickerViewController(
            forExportingURLs =  emptyList<Any>(),
        )
    }
    val pickerDelegate = remember {
        PickerDelegate(pickerArgs, onCancelled)
    }
    return remember {
        SelectFileLauncher {
            picker.setDelegate(pickerDelegate)
            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
        }
    }
}

private fun FileType.toUTType(): UTType? = when (this) {
    FileType.JSON -> UTType.typeWithFilenameExtension(this.typeExtension)
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
