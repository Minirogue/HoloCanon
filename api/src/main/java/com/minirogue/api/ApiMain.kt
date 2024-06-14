package com.minirogue.api

import com.holocanon.library.serialization.ext.internal.holocanonJson
import com.minirogue.api.media.getFullMediaList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString

private val mainScope = CoroutineScope(Job() + Dispatchers.Default)
private const val API_ROOT_DIRECTORY = "holocanon-api/"

internal fun main() {
    runBlocking {
        mainScope.launch {
            createVersionNumber(VERSION)
            createMediaResponses()
        }.join()
    }
}

private suspend fun createVersionNumber(version: Int) {
    JsonWriter.write(holocanonJson.encodeToString(version), API_ROOT_DIRECTORY + "version.json")
}

private suspend fun createMediaResponses() {
    val allMedia = getFullMediaList()
    JsonWriter.write(holocanonJson.encodeToString(allMedia), API_ROOT_DIRECTORY + "media.json")
    allMedia.forEach { starWarsMedia ->
        JsonWriter.write(
            holocanonJson.encodeToString(starWarsMedia),
            "${API_ROOT_DIRECTORY}media/${starWarsMedia.id}.json"
        )
    }
}
