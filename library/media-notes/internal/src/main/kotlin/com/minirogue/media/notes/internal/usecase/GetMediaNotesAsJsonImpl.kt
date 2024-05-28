package com.minirogue.media.notes.internal.usecase

import com.minirogue.media.notes.GetMediaNotesAsJson
import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import settings.usecase.GetCheckboxSettings
import javax.inject.Inject

class GetMediaNotesAsJsonImpl @Inject constructor(
    private val daoMedia: DaoMedia,
    private val getCheckboxSettings: GetCheckboxSettings,
) : GetMediaNotesAsJson {
    private val json = Json { prettyPrint = true }

    override suspend fun invoke(): Flow<String> {
        return getCheckboxSettings().combine(daoMedia.getAllMediaNotes()) { checkBoxSettings, allMediaNotes ->
            json.encodeToString(
                MediaNotesJsonV1(
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
                    }
                )
            )
        }
    }

    @Serializable
    private data class MediaNotesJsonV1(
        @SerialName("note_names") val checkboxNames: CheckBoxNamesV1,
        @SerialName("media_notes") val mediaNotes: List<MediaNotesV1>,
    )

    @Serializable
    private data class CheckBoxNamesV1(
        @SerialName("note_1_name") val name1: String,
        @SerialName("note_2_name") val name2: String,
        @SerialName("note_3_name") val name3: String,
    )

    @Serializable
    private data class MediaNotesV1(
        @SerialName("media_id") val mediaId: Long,
        @SerialName("note_1_checked") val checkBox1Value: Boolean,
        @SerialName("note_2_checked") val checkBox2Value: Boolean,
        @SerialName("note_3_checked") val checkBox3Value: Boolean,
    )
}