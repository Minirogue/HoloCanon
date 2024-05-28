package com.minirogue.media.notes

import kotlinx.coroutines.flow.Flow

interface GetMediaNotesAsJson {
    suspend operator fun invoke(): Flow<String>
}
