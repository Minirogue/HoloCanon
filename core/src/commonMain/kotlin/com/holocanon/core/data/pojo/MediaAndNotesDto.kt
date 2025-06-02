package com.holocanon.core.data.pojo

import androidx.room.Embedded
import com.holocanon.core.data.entity.MediaItemDto
import com.holocanon.core.data.entity.MediaNotesDto

data class MediaAndNotesDto(@Embedded val mediaItemDto: MediaItemDto, @Embedded val mediaNotesDto: MediaNotesDto)
