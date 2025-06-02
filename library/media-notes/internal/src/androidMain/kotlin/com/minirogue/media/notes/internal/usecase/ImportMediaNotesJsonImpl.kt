package com.minirogue.media.notes.internal.usecase

import android.content.res.Resources
import android.util.Log
import com.holocanon.core.data.dao.DaoMedia
import com.holocanon.core.data.entity.MediaNotesDto
import com.holocanon.feature.global.notification.usecase.SendGlobalToast
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.holocanon.library.media.notes.internal.R
import com.holocanon.library.media.notes.internal.model.MediaNotesJsonV1
import com.holocanon.library.media.notes.internal.model.MediaNotesV1
import com.minirogue.media.notes.ImportMediaNotesJson
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import settings.usecase.UpdateCheckboxName
import java.io.IOException
import java.io.InputStream

@Inject
@ContributesBinding(AppScope::class)
class ImportMediaNotesJsonImpl(
    private val updateCheckboxName: UpdateCheckboxName,
    private val daoMedia: DaoMedia,
    private val sendGlobalToast: SendGlobalToast,
    private val resources: Resources,
    private val dispatchers: HolocanonDispatchers,
    private val json: Json,
) : ImportMediaNotesJson {
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    override suspend fun invoke(inputStream: InputStream) = withContext(NonCancellable + dispatchers.io) {
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
            isBox3Checked = checkBox3Value,
        )

    companion object {
        private const val TAG = "ImportMediaNotesJson"
        private const val BOX_1 = 1
        private const val BOX_2 = 2
        private const val BOX_3 = 3
    }
}
