package com.minirogue.media.notes

import java.io.InputStream

interface ImportMediaNotesJson {
    operator fun invoke(inputStream: InputStream)
}