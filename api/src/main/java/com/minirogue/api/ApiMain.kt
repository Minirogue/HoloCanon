package com.minirogue.api

import com.minirogue.api.media.getFullMediaList
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val mainScope = CoroutineScope(Job() + Dispatchers.Default)
private val format = Json { prettyPrint = true }
private const val API_ROOT_DIRECTORY = "holocanon-api/"

fun main() {
    runBlocking {
        mainScope.launch {
            createVersionNumber(VERSION)
            createMediaResponses()
        }.join()
    }
}

private suspend fun createVersionNumber(version: Int) {
    JsonWriter.write(format.encodeToString(version), API_ROOT_DIRECTORY + "version.json")
}

private suspend fun createMediaResponses() {
    val allMedia = getFullMediaList()
    JsonWriter.write(format.encodeToString(allMedia), API_ROOT_DIRECTORY + "media.json")
    allMedia.forEach { starWarsMedia ->
        JsonWriter.write(
            format.encodeToString(starWarsMedia),
            "${API_ROOT_DIRECTORY}media/${starWarsMedia.id}.json"
        )
    }
}
