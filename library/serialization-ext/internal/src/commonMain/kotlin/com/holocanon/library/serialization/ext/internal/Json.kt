package com.holocanon.library.serialization.ext.internal

import kotlinx.serialization.json.Json

val holocanonJson = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}