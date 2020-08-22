import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            route("/") {
                get {
                    call.respondText("My Example Blog", ContentType.Text.Html)
                }
                route("book/{id}") {
                    get {
                            val id = call.parameters["id"]?.toLongOrNull()
                            if (id != null) {
                                val data = Json.encodeToString(Book(id, "A Star Wars Book with id $id"))
                                //call.respondText("Id: $id")
                                call.respondText(data)
                            } else call.respondText("Error: misformated ID")
                    }
                }
            }
        }
    }.start(wait = true)
}
