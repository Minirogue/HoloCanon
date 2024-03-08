package com.minirogue.starwarscanontracker.core.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.pojo.MediaAndNotes
import filters.model.MediaFilter

interface GetMediaListWithNotes {
    suspend operator fun invoke(filterList: List<MediaFilter>): LiveData<List<MediaAndNotes>>
}

