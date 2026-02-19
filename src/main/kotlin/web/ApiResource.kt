package web

import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import service.LibraryService

fun Route.api(service: LibraryService) {
    post("/api/users") {
        val req = call.receive<Map<String, String>>()
        val user = service.createUser(req["username"]!!, req["email"]!!)
        call.respond(user)
    }
    get("/api/users") {
        call.respond(service.getAllUsers())
    }

    post("/api/books") {
        val req = call.receive<Map<String, String>>()
        val book = service.createBook(req["title"]!!, req["author"]!!)
        call.respond(book)
    }
    get("/api/books") {
        call.respond(service.getAllBooks())
    }
    get("/api/books/search") {
        val keyword = call.request.queryParameters["q"] ?: ""
        val lowAccessibleOnly = call.request.queryParameters["lowAccessible"]?.toBoolean() ?: false
        
        val results = if (lowAccessibleOnly) {
            service.searchBooksLowAccessible(keyword)
        } else {
            service.searchBooks(keyword)
        }
        call.respond(results)
    }
    get("/api/books/{id}") {
        val id = call.parameters["id"]!!.toInt()
        call.respond(service.getBook(id) ?: mapOf("error" to "not found"))
    }

    post("/api/loans") {
        val req = call.receive<Map<String, Int>>()
        val loan = service.borrowBook(req["userId"]!!, req["bookId"]!!)
        call.respond(loan)
    }
    get("/api/loans/user/{userId}") {
        val userId = call.parameters["userId"]!!.toInt()
        call.respond(service.getUserLoans(userId))
    }
    get("/api/loans/user/{userId}/active") {
        val userId = call.parameters["userId"]!!.toInt()
        call.respond(service.getActiveLoansByUser(userId))
    }
    post("/api/loans/{loanId}/return") {
        val loanId = call.parameters["loanId"]!!.toInt()
        service.returnBook(loanId)
        call.respond(mapOf("status" to "returned"))
    }
}