package com.minirogue.starwarscanontracker.core.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto

interface GetNotesForMedia {
    operator fun invoke(itemId: Int): LiveData<MediaNotesDto>
}
