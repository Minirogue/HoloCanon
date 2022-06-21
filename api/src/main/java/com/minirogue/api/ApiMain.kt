package com.minirogue.api

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream

fun main(args: Array<String>): Unit = EngineMain.main(args)
// {
//     embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
//         install(ContentNegotiation) {
//             json(Json {
//                 prettyPrint = true
//                 isLenient = true
//             })
//         }
//         configureRouting()
//     }.start(wait = true)
// }

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/media") { // TODO ensure coroutines are used
            val returnValue = mutableListOf<StarWarsMedia>()
            val stream = getResourceAsStream("media.csv")
            val reader = stream.reader()
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim())
            for (csvRecord in csvParser) {
                returnValue.add(
                    StarWarsMedia(id = csvRecord.get("id").toLong(),
                        title = csvRecord.get("title"),
                        type = csvRecord.get("type"))
                )
            }
            call.respond(returnValue)
        }
    }
}

@Serializable
data class StarWarsMedia(val id: Long, val title: String, val type: String)

fun getResourceAsStream(resource: String): InputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(
    resource)
