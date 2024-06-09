package com.minirogue.media.notes.internal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)

@Serializable
internal data class MediaNotesV1(
    @SerialName("media_id") val mediaId: Long,
    @SerialName("note_1_checked") val checkBox1Value: Boolean,
    @SerialName("note_2_checked") val checkBox2Value: Boolean,
    @SerialName("note_3_checked") val checkBox3Value: Boolean,
)