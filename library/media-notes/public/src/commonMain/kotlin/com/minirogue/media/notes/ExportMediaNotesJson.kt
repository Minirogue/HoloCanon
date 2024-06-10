package com.minirogue.media.notes

import java.io.OutputStream

interface ExportMediaNotesJson {
    operator fun invoke(outputStream: OutputStream)
}
