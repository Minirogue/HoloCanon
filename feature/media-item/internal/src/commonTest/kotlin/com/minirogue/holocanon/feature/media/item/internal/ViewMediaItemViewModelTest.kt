package com.minirogue.holocanon.feature.media.item.internal

import FakeGetCheckboxSettings
import FakeIsNetworkAllowed
import com.holocanon.library.coroutine.ext.CoroutineTest
import com.holocanon.library.media.item.usecase.FakeGetMedia
import com.holocanon.library.media.notes.usecase.FakeGetNotesForMedia
import com.holocanon.library.media.notes.usecase.FakeUpdateCheckValue
import com.minirogue.common.model.Company
import com.minirogue.common.model.MediaType
import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.CheckBoxNumber
import com.minirogue.media.notes.model.MediaNotes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import settings.model.CheckboxSetting
import settings.model.CheckboxSettings
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ViewMediaItemViewModelTest : CoroutineTest {

    // Test doubles
    private lateinit var getMedia: FakeGetMedia
    private lateinit var getNotesForMedia: FakeGetNotesForMedia
    private lateinit var updateNotes: FakeUpdateCheckValue
    private lateinit var isNetworkAllowed: FakeIsNetworkAllowed
    private lateinit var getCheckboxSettings: FakeGetCheckboxSettings

    // Test class
    private lateinit var viewModel: ViewMediaItemViewModel

    // Dummy data
    private val testItemId: Long = 1337L
    val dummyMediaItem = StarWarsMedia(
        id = testItemId,
        title = "title",
        type = MediaType.NOVELIZATION,
        imageUrl = null,
        releaseDate = "10/10/2020",
        timeline = null,
        description = null,
        series = null,
        number = null,
        publisher = Company.RANDOM_HOUSE
    )
    val dummyNotes = MediaNotes(isBox1Checked = true, isBox2Checked = false, isBox3Checked = true)
    val dummyCheckboxSettings = CheckboxSettings(
        checkbox1Setting = CheckboxSetting("1", false),
        checkbox2Setting = CheckboxSetting("2", true),
        checkbox3Setting = CheckboxSetting("3", false)
    )

    @BeforeTest
    fun setup() {
        getMedia = FakeGetMedia()
        getNotesForMedia = FakeGetNotesForMedia()
        updateNotes = FakeUpdateCheckValue()
        isNetworkAllowed = FakeIsNetworkAllowed()
        getCheckboxSettings = FakeGetCheckboxSettings()

        viewModel = ViewMediaItemViewModel(
            itemId = testItemId,
            getMedia = getMedia,
            getNotesForMedia = getNotesForMedia,
            updateNotes = updateNotes,
            isNetworkAllowed = isNetworkAllowed,
            getCheckboxSettings = getCheckboxSettings
        )
    }

    @Test
    fun `state is initialized with values from use cases`() = runTest {
        // Arrange
        getMedia.emit(dummyMediaItem)
        getNotesForMedia.emit(dummyNotes)
        isNetworkAllowed.emit(true)
        getCheckboxSettings.emit(dummyCheckboxSettings)

        // Act
        val state = viewModel.state.first()

        // Assert
        with(state) {
            assertEquals(dummyMediaItem, mediaItem)
            assertEquals(dummyNotes, mediaNotes)
            assertEquals(dummyCheckboxSettings, checkboxSettings)
            assertTrue(isNetworkAllowed)
        }
    }

    @Test
    fun `toggleCheckbox1 with true updates correct checkbox to true`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox1(testItemId, true).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox1,
            expectedMediaId = testItemId,
            expectedNewValue = true
        )
    }

    @Test
    fun `toggleCheckbox1 with false updates correct checkbox to false`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox1(testItemId, false).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox1,
            expectedMediaId = testItemId,
            expectedNewValue = false
        )
    }

    @Test
    fun `toggleCheckbox2 with true updates correct checkbox to true`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox2(testItemId, true).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox2,
            expectedMediaId = testItemId,
            expectedNewValue = true
        )
    }

    @Test
    fun `toggleCheckbox2 with false updates correct checkbox to false`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox2(testItemId, false).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox2,
            expectedMediaId = testItemId,
            expectedNewValue = false
        )
    }

    @Test
    fun `toggleCheckbox3 with true updates correct checkbox to true`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox3(testItemId, true).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox3,
            expectedMediaId = testItemId,
            expectedNewValue = true
        )
    }

    @Test
    fun `toggleCheckbox3 with false updates correct checkbox to false`() = runTest {
        // Arrange

        // Act
        viewModel.toggleCheckbox3(testItemId, false).join()

        // Assert
        updateNotes.assertInvoked(
            expectedCheckboxNumber = CheckBoxNumber.CheckBox3,
            expectedMediaId = testItemId,
            expectedNewValue = false
        )
    }
}