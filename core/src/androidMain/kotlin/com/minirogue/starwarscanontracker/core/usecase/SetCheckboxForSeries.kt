package com.minirogue.starwarscanontracker.core.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
class SetCheckboxForSeries(private val daoMedia: DaoMedia) {
    // A Mutex in case notes are being updated concurrently (e.g. user clicks on two separate checkboxes for a series)
    private val updatingNotesMutex = Mutex()

    suspend operator fun invoke(
        checkbox: Checkbox,
        seriesId: Int,
        newValue: Boolean,
    ) = withContext(Dispatchers.Default) {
        updatingNotesMutex.withLock {
            val listOfNotes = daoMedia.getMediaNotesBySeriesNonLive(seriesId)
            for (notes in listOfNotes) {
                when (checkbox) {
                    Checkbox.CHECKBOX_1 -> notes.isBox1Checked = newValue
                    Checkbox.CHECKBOX_2 -> notes.isBox2Checked = newValue
                    Checkbox.CHECKBOX_3 -> notes.isBox3Checked = newValue
                }
                daoMedia.update(notes)
            }
        }
    }
}

enum class Checkbox { CHECKBOX_1, CHECKBOX_2, CHECKBOX_3 }
