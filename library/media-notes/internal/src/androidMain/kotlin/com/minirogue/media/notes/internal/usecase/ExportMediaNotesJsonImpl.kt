package com.minirogue.media.notes.internal.usecase

import android.content.res.Resources
import android.util.Log
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.holocanon.library.media.notes.internal.R
import com.holocanon.library.media.notes.internal.model.CheckBoxNamesV1
import com.holocanon.library.media.notes.internal.model.MediaNotesJsonV1
import com.holocanon.library.media.notes.internal.model.MediaNotesV1
import com.minirogue.media.notes.ExportMediaNotesJson
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import settings.usecase.GetCheckboxSettings
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception

@Inject
@ContributesBinding(AppScope::class)
class ExportMediaNotesJsonImpl(
    private val daoMedia: DaoMedia,
    private val getCheckboxSettings: GetCheckboxSettings,
    private val sendGlobalToast: SendGlobalToast,
    private val resources: Resources,
    private val dispatchers: HolocanonDispatchers,
    private val json: Json,
) : ExportMediaNotesJson {
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override suspend fun invoke(outputStream: OutputStream) = withContext(NonCancellable + dispatchers.io) {
        val checkBoxSettings = getCheckboxSettings().first()
        val allMediaNotes = daoMedia.getAllMediaNotes().first()

        try {
            json.encodeToStream(
                value = MediaNotesJsonV1(
                    CheckBoxNamesV1(
                        name1 = checkBoxSettings.checkbox1Setting.name,
                        name2 = checkBoxSettings.checkbox2Setting.name,
                        name3 = checkBoxSettings.checkbox3Setting.name,
                    ),
                    allMediaNotes.map { mediaNotesDto ->
                        MediaNotesV1(
                            mediaId = mediaNotesDto.mediaId.toLong(),
                            checkBox1Value = mediaNotesDto.isBox1Checked,
                            checkBox2Value = mediaNotesDto.isBox2Checked,
                            checkBox3Value = mediaNotesDto.isBox3Checked,
                        )
                    },
                ),
                stream = outputStream,
            )
            outputStream.close()
            onSuccess()
        } catch (e: IOException) {
            onFailed(e)
        } catch (e: SerializationException) {
            onFailed(e)
        }
    }

    private fun onFailed(exception: Exception) {
        Log.e(TAG, "Failed to export media notes", exception)
        sendGlobalToast(resources.getString(R.string.media_notes_there_was_an_error_exporting_your_data))
    }

    private fun onSuccess() {
        sendGlobalToast(resources.getString(R.string.media_notes_data_exported))
    }

    companion object {
        private const val TAG = "ExportMediaNotesImpl"
    }
}
