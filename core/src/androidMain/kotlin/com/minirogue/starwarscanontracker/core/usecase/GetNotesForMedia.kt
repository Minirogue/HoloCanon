package com.minirogue.starwarscanontracker.core.usecase

import androidx.lifecycle.LiveData
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto
import kotlinx.coroutines.flow.Flow

interface GetNotesForMedia {
    operator fun invoke(itemId: Int): Flow<MediaNotesDto>
}
