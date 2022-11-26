package com.minirogue.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class JsonWriter {
    companion object {
        private val fileMutex = Mutex()
        suspend fun write(json: String, path: String) = withContext(Dispatchers.IO) {
            launch {
                fileMutex.withLock {
                    File(path).parentFile.mkdirs()
                    BufferedWriter(FileWriter(path, false)).use { it.write(json) }
                }
            }
        }
    }
}
