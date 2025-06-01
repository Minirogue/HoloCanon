package com.minirogue.media.notes

import java.io.InputStream

interface ImportMediaNotesJson {
    suspend operator fun invoke(inputStream: InputStream)
}
