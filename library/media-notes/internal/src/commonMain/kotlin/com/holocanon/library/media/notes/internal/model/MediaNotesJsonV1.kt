package com.holocanon.library.media.notes.internal.model

import com.holocanon.core.data.entity.MediaNotesDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import settings.model.CheckboxSettings

@Serializable
internal data class MediaNotesJsonV1(
    @SerialName("note_names") val checkboxNames: CheckBoxNamesV1,
    @SerialName("media_notes") val mediaNotes: List<MediaNotesV1>,
)

@Serializable
internal data class CheckBoxNamesV1(
    @SerialName("note_1_name") val name1: String,
    @SerialName("note_2_name") val name2: String,
    @SerialName("note_3_name") val name3: String,
) {
    companion object {
        fun fromCheckboxSettings(checkBoxSettings: CheckboxSettings): CheckBoxNamesV1 =
            CheckBoxNamesV1(
                name1 = checkBoxSettings.checkbox1Setting.name,
                name2 = checkBoxSettings.checkbox2Setting.name,
                name3 = checkBoxSettings.checkbox3Setting.name,
            )
    }
}

@Serializable
internal data class MediaNotesV1(
    @SerialName("media_id") val mediaId: Long,
    @SerialName("note_1_checked") val checkBox1Value: Boolean,
    @SerialName("note_2_checked") val checkBox2Value: Boolean,
    @SerialName("note_3_checked") val checkBox3Value: Boolean,
) {
    companion object {
        fun fromMediaNotesDto(mediaNotesDto: MediaNotesDto): MediaNotesV1 = MediaNotesV1(
            mediaId = mediaNotesDto.mediaId.toLong(),
            checkBox1Value = mediaNotesDto.isBox1Checked,
            checkBox2Value = mediaNotesDto.isBox2Checked,
            checkBox3Value = mediaNotesDto.isBox3Checked,
        )
    }
}
