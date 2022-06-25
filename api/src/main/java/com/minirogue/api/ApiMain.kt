package com.minirogue.api

import com.minirogue.api.media.getFullMediaList
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // referenced in application.conf
fun Application.configureRouting() {
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        get("/") {
            call.respondText("This is the Holocanon API. Still a work-in-progress. More to come.")
        }
        get("/media") {
            val returnValue = getFullMediaList()
            call.respond(returnValue)
        }
        get("/version") { call.respond(21) }
    }
}
