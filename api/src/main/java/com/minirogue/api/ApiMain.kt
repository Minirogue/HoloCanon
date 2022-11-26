package com.minirogue.api

import com.minirogue.api.media.getFullMediaList
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val mainScope = CoroutineScope(Job() + Dispatchers.Default)
private val format = Json { prettyPrint = true }
private const val apiRootDirectory = "holocanon-api/"

fun main(args: Array<String>) {
    runBlocking {
        mainScope.launch {
            createVersionNumber(22)
            createMediaResponses()
        }.join()
    }
}

private suspend fun createVersionNumber(version: Int) {
    JsonWriter.write(format.encodeToString(version), apiRootDirectory + "version.json")
}

private suspend fun createMediaResponses() {
    val allMedia = getFullMediaList()
    JsonWriter.write(format.encodeToString(allMedia), apiRootDirectory + "media.json")
    allMedia.forEach { starWarsMedia ->
        JsonWriter.write(
            format.encodeToString(starWarsMedia),
            "${apiRootDirectory}media/${starWarsMedia.id}.json"
        )
    }
}
