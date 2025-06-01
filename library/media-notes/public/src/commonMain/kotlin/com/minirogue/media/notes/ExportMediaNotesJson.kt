package com.minirogue.media.notes

import java.io.OutputStream

interface ExportMediaNotesJson {
    suspend operator fun invoke(outputStream: OutputStream)
}
