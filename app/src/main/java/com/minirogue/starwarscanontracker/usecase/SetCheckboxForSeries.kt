package com.minirogue.starwarscanontracker.usecase

import com.minirogue.starwarscanontracker.core.model.room.dao.DaoMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class SetCheckboxForSeries @Inject constructor(private val daoMedia: DaoMedia) {
    // A Mutex in case notes are being updated concurrently (e.g. user clicks on two separate checkboxes for a series)
    private val updatingNotesMutex = Mutex()

    operator fun invoke(
        checkbox: Checkbox,
        seriesId: Int,
        newValue: Boolean,
    ) = GlobalScope.launch(Dispatchers.Default) {
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
