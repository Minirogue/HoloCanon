package com.minirogue.media.notes.internal.usecase

import android.content.res.Resources
import android.util.Log
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import com.holocanon.library.coroutine.ext.ApplicationScope
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.minirogue.holocanon.library.media.notes.internal.R
import com.minirogue.media.notes.ImportMediaNotesJson
import com.minirogue.media.notes.internal.model.MediaNotesJsonV1
import com.minirogue.media.notes.internal.model.MediaNotesV1
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import settings.usecase.UpdateCheckboxName
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class ImportMediaNotesJsonImpl @Inject constructor(
    private val updateCheckboxName: UpdateCheckboxName,
    private val daoMedia: DaoMedia,
    private val sendGlobalToast: SendGlobalToast,
    private val resources: Resources,
    private val appScope: ApplicationScope,
    private val dispatchers: HolocanonDispatchers,
    private val json: Json,
) : ImportMediaNotesJson {
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override fun invoke(inputStream: InputStream) {
        appScope.launch(dispatchers.io) {
            try {
                val mediaNotesJsonDto = json.decodeFromStream<MediaNotesJsonV1>(inputStream)
                inputStream.close()
                launch { updateCheckboxName(BOX_1, mediaNotesJsonDto.checkboxNames.name1) }
                launch { updateCheckboxName(BOX_2, mediaNotesJsonDto.checkboxNames.name2) }
                launch { updateCheckboxName(BOX_3, mediaNotesJsonDto.checkboxNames.name3) }
                daoMedia.clearAllMediaNotes()
                mediaNotesJsonDto.mediaNotes.map { notes ->
                    launch { daoMedia.insert(notes.toRoomDto()) }
                }.joinAll()
                onSuccess()
            } catch (e: IllegalArgumentException) {
                onFailed(e)
            } catch (e: SerializationException) {
                onFailed(e)
            } catch (e: IOException) {
                onFailed(e)
            }
        }
    }

    private fun onFailed(e: Exception) {
        Log.e(TAG, "Failed to parse Media Notes JSON", e)
        sendGlobalToast(resources.getString(R.string.media_notes_there_was_an_error_importing_your_data))
    }

    private fun onSuccess() {
        sendGlobalToast(resources.getString(R.string.media_notes_data_imported))
    }

    private fun MediaNotesV1.toRoomDto(): MediaNotesDto =
        MediaNotesDto(
            mediaId = mediaId.toInt(),
            isBox1Checked = checkBox1Value,
            isBox2Checked = checkBox2Value,
            isBox3Checked = checkBox3Value
        )

    companion object {
        private const val TAG = "ImportMediaNotesJson"
        private const val BOX_1 = 1
        private const val BOX_2 = 2
        private const val BOX_3 = 3
    }
}
