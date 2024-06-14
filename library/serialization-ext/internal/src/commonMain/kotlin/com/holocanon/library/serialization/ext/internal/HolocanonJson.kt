package com.holocanon.library.serialization.ext.internal

import kotlinx.serialization.json.Json

object HolocanonJson {
    operator fun invoke(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
}
