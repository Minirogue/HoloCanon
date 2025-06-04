package com.minirogue.media.notes

import kotlinx.io.RawSource

interface ImportMediaNotesJson {
    suspend operator fun invoke(inputStream: RawSource)
}
