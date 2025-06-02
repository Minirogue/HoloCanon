package com.minirogue.series.usecase

import com.holocanon.core.data.dao.DaoMedia
import com.minirogue.series.model.Checkbox
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Inject
@ContributesBinding(AppScope::class)
class SetCheckboxForSeriesImpl(private val daoMedia: DaoMedia) : SetCheckboxForSeries {
    // A Mutex in case notes are being updated concurrently (e.g. user clicks on two separate checkboxes for a series)
    private val updatingNotesMutex = Mutex()

    override suspend operator fun invoke(
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
