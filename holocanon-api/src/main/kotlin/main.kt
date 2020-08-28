
import model.Book


fun main(args: Array<String>) {
    val book = Book(2, "A Star Wars model.Book with id 2")
//    embeddedServer(Netty, 8080) {
////        install(ContentNegotiation) {
////            json()
////        }
//        routing {
//            route("/") {
//                get {
//                    call.respondText("My Example Blog", ContentType.Text.Html)
//                }
//                route("book/{id}") {
//                    get {
//                            val id = call.parameters["id"]?.toLongOrNull()
//                            if (id != null) {
//                                val data = "book"//Json.encodeToString(book)
//                                //call.respondText("Id: $id")
//                                call.respondText(data)
//                            } else call.respondText("Error: misformated ID")
//                    }
//                }
//            }
//        }
//    }.start(wait = true)
}
