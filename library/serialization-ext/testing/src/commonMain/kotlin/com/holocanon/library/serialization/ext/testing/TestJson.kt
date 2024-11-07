package com.holocanon.library.serialization.ext.testing

import kotlinx.serialization.json.Json

object TestJson {
    operator fun invoke(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
}
