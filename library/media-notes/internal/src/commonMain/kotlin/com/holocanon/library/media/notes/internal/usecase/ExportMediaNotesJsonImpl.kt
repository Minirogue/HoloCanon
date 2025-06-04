package com.holocanon.library.media.notes.internal.usecase

import android.content.res.Resources
import android.util.Log
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.holocanon.library.media.notes.internal.R
import com.holocanon.library.media.notes.internal.model.CheckBoxNamesV1
import com.holocanon.library.media.notes.internal.model.MediaNotesJsonV1
import com.holocanon.library.media.notes.internal.model.MediaNotesV1
import com.minirogue.media.notes.ExportMediaNotesJson
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import holocanon.library.media_notes.internal.generated.resources.Res
import holocanon.library.media_notes.internal.generated.resources.media_notes_data_exported
import holocanon.library.media_notes.internal.generated.resources.media_notes_there_was_an_error_exporting_your_data
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.io.RawSink
import kotlinx.io.buffered
import kotlinx.io.writeString
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.encodeToSink
import org.jetbrains.compose.resources.getString
import settings.usecase.GetCheckboxSettings
import java.io.IOException
import java.lang.Exception

@Inject
@ContributesBinding(AppScope::class)
class ExportMediaNotesJsonImpl(
    private val daoMedia: DaoMedia,
    private val getCheckboxSettings: GetCheckboxSettings,
    private val sendGlobalToast: SendGlobalToast,
    private val dispatchers: HolocanonDispatchers,
    private val json: Json,
) : ExportMediaNotesJson {
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override suspend fun invoke(outputStream: RawSink) =
        withContext(NonCancellable + dispatchers.io) {
            val checkBoxSettings = getCheckboxSettings().first()
            val allMediaNotes = daoMedia.getAllMediaNotes().first()

            try {
                outputStream.buffered().use {
                    json.encodeToSink(
                        value = MediaNotesJsonV1(
                            CheckBoxNamesV1.fromCheckboxSettings(checkBoxSettings),
                            allMediaNotes.map { mediaNotesDto ->
                                MediaNotesV1.fromMediaNotesDto(mediaNotesDto)
                            },
                        ),
                        sink = it
                    )
                }

                onSuccess()
            } catch (e: IOException) {
                onFailed(e)
            } catch (e: SerializationException) {
                onFailed(e)
            }
        }

    private suspend fun onFailed(exception: Exception) {
        Log.e(TAG, "Failed to export media notes", exception)
        sendGlobalToast(getString(Res.string.media_notes_there_was_an_error_exporting_your_data))
    }

    private suspend fun onSuccess() {
        sendGlobalToast(getString(Res.string.media_notes_data_exported))
    }

    companion object {
        private const val TAG = "ExportMediaNotesImpl"
    }
}
