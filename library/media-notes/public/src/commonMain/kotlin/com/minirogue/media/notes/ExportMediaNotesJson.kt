package com.minirogue.media.notes

import kotlinx.io.RawSink

interface ExportMediaNotesJson {
    suspend operator fun invoke(outputStream: RawSink)
}
