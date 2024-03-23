package com.minirogue.starwarscanontracker.core.model.room.pojo

import androidx.room.Embedded
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaItemDto
import com.minirogue.starwarscanontracker.core.model.room.entity.MediaNotesDto

data class MediaAndNotesDto(@Embedded val mediaItemDto: MediaItemDto, @Embedded val mediaNotesDto: MediaNotesDto)
