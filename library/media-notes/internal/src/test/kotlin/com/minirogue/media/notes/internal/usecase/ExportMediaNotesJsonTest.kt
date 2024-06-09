package com.minirogue.media.notes.internal.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import kotlin.test.Test

class ExportMediaNotesJsonTest {

    private val daoMedia: DaoMedia = mockk()
    private val getCheckboxSettings: GetCheckboxSettings = mockk()

    private val getMediaNotesAsJson = ExportMediaNotesJsonImpl(daoMedia, getCheckboxSettings)

    @Test
    fun `getMediaNotesAsJson returns a formatted json string`() = runTest {
        // Arrange
        every { daoMedia.getAllMediaNotes() } returns flowOf(
            listOf(
                MediaNotesDto(
                    mediaId = 1,
                    isBox1Checked = true,
                    isBox2Checked = false,
                    isBox3Checked = true
                )
            )
        )
        every { getCheckboxSettings() } returns flowOf(
            CheckboxSettings(
                CheckboxSetting("box1", true),
                CheckboxSetting("box2", true),
                CheckboxSetting("box3", false),
            )
        )

        // Act
        val result = getMediaNotesAsJson().first()

        // Assert
        assert(
            result == """{
    "note_names": {
        "note_1_name": "box1",
        "note_2_name": "box2",
        "note_3_name": "box3"
    },
    "media_notes": [
        {
            "media_id": 1,
            "note_1_checked": true,
            "note_2_checked": false,
            "note_3_checked": true
        }
    ]
}""".replace(System.lineSeparator(), ""),
            { "media notes as Json failed" })
    }

}