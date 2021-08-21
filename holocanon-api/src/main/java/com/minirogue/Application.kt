package com.minirogue

import CompanyDto
import MediaTypeDto
import PersonDto
import SeriesDto
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull() ?: 8000) {
        CoroutineScope(Job() + Dispatchers.Default).launch {
            val databaseAccessor = DependencyInjectorImpl()
                .getDatabaseAccessor().apply { initializeFromResources(Dispatchers.Default) }
            install(ContentNegotiation) { json() }
            routing {
                // TODO add versioning routing, which includes compatibility with current API and database version
                peopleRouting(databaseAccessor)
                companyRouting(databaseAccessor)
                seriesRouting(databaseAccessor)
                mediaTypeRouting(databaseAccessor)
                mediaRouting(databaseAccessor)
            }
        }
    }.start(wait = true)
}

private fun Routing.peopleRouting(databaseAccessor: DatabaseAccessor) {
    get("/author/{id}") {
        call.parameters["id"]?.toLongOrNull()?.also {
            val author = databaseAccessor.getPerson(it) ?: PersonDto(-1, "MissingNo")
            call.respond(author)
        }
    }
    get("/author") {
        val allAuthors = databaseAccessor.getAllPeople()
        call.respond(allAuthors)
    }
}

private fun Routing.companyRouting(databaseAccessor: DatabaseAccessor) {
    get("/company") {
        val allCompanies = databaseAccessor.getAllCompanies()
        call.respond(allCompanies)
    }
    get("/company/{id}") {
        call.parameters["id"]?.toLongOrNull()?.also {
            val company = databaseAccessor.getCompany(it) ?: CompanyDto(-1, "MissingNo")
            call.respond(company)
        }
    }
}

private fun Routing.seriesRouting(databaseAccessor: DatabaseAccessor) {
    get("/series") {
        val allSeries = databaseAccessor.getAllSeries()
        call.respond(allSeries)
    }
    get("/series/{id}") {
        call.parameters["id"]?.toLongOrNull()?.also {
            val series = databaseAccessor.getSeries(it) ?: SeriesDto(-1, "MissingNo")
            call.respond(series)
        }
    }
}

private fun Routing.mediaTypeRouting(databaseAccessor: DatabaseAccessor) {
    get("/media_type") {
        val allTypes = databaseAccessor.getAllMediaTypes()
        call.respond(allTypes)
    }
    get("/media_type/{id}") {
        call.parameters["id"]?.toLongOrNull()?.also {
            val type = databaseAccessor.getMediaType(it) ?: MediaTypeDto(-1, "MissingNo")
            call.respond(type)
        }
    }
}

private fun Routing.mediaRouting(databaseAccessor: DatabaseAccessor) {
    get("/media") {
        val allMedia = databaseAccessor.getAllMedia()
        call.respond(allMedia)
    }
    get("/media/{id}") {
        call.parameters["id"]?.toLongOrNull()?.also { id ->
            val media = databaseAccessor.getMedia(id)
            media?.let { call.respond(it) }
        }
    }
}
