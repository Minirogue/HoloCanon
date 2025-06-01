package com.minirogue.media.notes.internal.usecase

import android.content.res.Resources
import com.holocanon.feature.global.notification.usecase.FakeSendGlobalToast
import com.holocanon.library.coroutine.ext.CoroutineTestRule
import com.holocanon.library.coroutine.ext.HolocanonDispatchers
import com.holocanon.library.serialization.ext.testing.TestJson
import com.holocanon.core.data.dao.DaoMedia
import com.minirogue.starwarscanontracker.core.data.entity.MediaNotesDto
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Rule
import org.junit.Test
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import settings.usecase.GetCheckboxSettings
import java.io.ByteArrayOutputStream

class ExportMediaNotesJsonTest {
    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val daoMedia: com.holocanon.core.data.dao.DaoMedia = mockk()
    private val getCheckboxSettings: GetCheckboxSettings = mockk()
    private val sendGlobalToast: FakeSendGlobalToast = FakeSendGlobalToast()
    private val resources: Resources = mockk()
    private val json: Json = TestJson()

    private val holocanonDispatchers: HolocanonDispatchers = coroutineTestRule.holocanonDispatchers

    private val getMediaNotesAsJson = ExportMediaNotesJsonImpl(
        daoMedia = daoMedia,
        getCheckboxSettings = getCheckboxSettings,
        sendGlobalToast = sendGlobalToast,
        resources = resources,
        dispatchers = holocanonDispatchers,
        json = json,
    )

    @Test
    fun `ExportMediaNotesJson correctly writes a formatted json to the given stream`() = runTest {
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
        every {resources.getString(any())} returns "toast message"
        val outputStream = ByteArrayOutputStream()

        // Act
        getMediaNotesAsJson(outputStream)
        runCurrent()
        val result = outputStream.toString()

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
}"""
        ) { "media notes not written as expected" }
    }

}
