import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import service.DatabaseFactory
import service.LibraryService
import util.JsonMapper
import web.index
import web.api

fun Application.module() {
    install(ContentNegotiation) {
        json(JsonMapper.defaultMapper)
    }

    DatabaseFactory.connectAndMigrate()
    val service = LibraryService()

    routing {
        staticResources("", "static")
        index()
        api(service)
    }
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}