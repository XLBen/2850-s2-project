package web

import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Route.index() {
    get("/") {
        call.respondRedirect("/index.html")
    }
}