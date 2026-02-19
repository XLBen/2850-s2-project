package service

import model.*
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import service.DatabaseFactory.dbExec

class LibraryService {

    suspend fun createUser(username: String, email: String): User {
        var id = 0
        dbExec {
            id = (Users.insert {
                it[Users.username] = username
                it[Users.email] = email
            } get Users.id)
        }
        return User(id, username, email)
    }

    suspend fun getAllUsers() = dbExec { 
        Users.selectAll().map { User(it[Users.id], it[Users.username], it[Users.email]) } 
    }

    suspend fun createBook(title: String, author: String, shelfId: Int? = null): Book {
        var id = 0
        dbExec {
            id = (Books.insert {
                it[Books.title] = title
                it[Books.author] = author
                it[Books.status] = "available"
                if (shelfId != null) it[Books.shelfId] = shelfId
            } get Books.id)
        }
        return Book(id, title, author, "available", shelfId)
    }

    suspend fun getAllBooks() = dbExec { 
        Books.selectAll().map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status], it[Books.shelfId]) } 
    }

    suspend fun searchBooks(keyword: String) = dbExec {
        Books.selectAll()
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status], it[Books.shelfId]) }
            .filter { it.title.contains(keyword, ignoreCase = true) || it.author.contains(keyword, ignoreCase = true) }
    }

    suspend fun searchBooksLowAccessible(keyword: String) = dbExec {
        val books = Books.selectAll()
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status], it[Books.shelfId]) }
            .filter { it.title.contains(keyword, ignoreCase = true) || it.author.contains(keyword, ignoreCase = true) }
        
        val lowAccessibleShelfIds = Shelves.selectAll()
            .where { Shelves.isLowAccessible eq true }
            .map { it[Shelves.id] }
        
        books.filter { it.shelfId in lowAccessibleShelfIds }
    }

    suspend fun getBook(id: Int) = dbExec {
        Books.selectAll().where { Books.id eq id }
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status], it[Books.shelfId]) }
            .firstOrNull()
    }

    suspend fun updateBookStatus(bookId: Int, status: String) {
        dbExec { 
            Books.update({ Books.id eq bookId }) { it[Books.status] = status } 
        }
    }

    suspend fun borrowBook(userId: Int, bookId: Int): Loan {
        updateBookStatus(bookId, "borrowed")
        var id = 0
        dbExec {
            id = (Loans.insert {
                it[Loans.userId] = userId
                it[Loans.bookId] = bookId
                it[Loans.borrowDate] = System.currentTimeMillis()
            } get Loans.id)
        }
        return Loan(id, userId, bookId, System.currentTimeMillis())
    }

    suspend fun returnBook(loanId: Int) {
        val bookId = dbExec {
            val loan = Loans.selectAll().where { Loans.id eq loanId }.firstOrNull()
            if (loan != null) {
                Loans.update({ Loans.id eq loanId }) { it[Loans.returnDate] = System.currentTimeMillis() }
                loan[Loans.bookId]
            } else {
                null
            }
        }
        if (bookId != null) {
            updateBookStatus(bookId, "available")
        }
    }

    suspend fun debugAdjustLoanDate(loanId: Int, daysToSubtract: Int) {
        dbExec {
            val loan = Loans.selectAll().where { Loans.id eq loanId }.firstOrNull()
            if (loan != null) {
                val newDate = loan[Loans.borrowDate] - (daysToSubtract * 24L * 60 * 60 * 1000)
                Loans.update({ Loans.id eq loanId }) {
                    it[Loans.borrowDate] = newDate
                }
            }
        }
    }

    suspend fun getUserLoans(userId: Int) = dbExec {
        Loans.selectAll().where { Loans.userId eq userId }
            .map { Loan(it[Loans.id], it[Loans.userId], it[Loans.bookId], it[Loans.borrowDate], it[Loans.returnDate]) }
    }

    suspend fun getActiveLoansByUser(userId: Int) = dbExec {
        Loans.selectAll()
            .map { Loan(it[Loans.id], it[Loans.userId], it[Loans.bookId], it[Loans.borrowDate], it[Loans.returnDate]) }
            .filter { it.userId == userId && it.returnDate == null }
    }

    suspend fun saveAccessibility(userId: Int, fontSizeLevel: Int, isHighContrast: Boolean): AccessibilitySetting = dbExec {
        AccessibilitySettings.update({ AccessibilitySettings.userId eq userId }) {
            it[AccessibilitySettings.fontSizeLevel] = fontSizeLevel
            it[AccessibilitySettings.isHighContrast] = isHighContrast
        }
        val result = AccessibilitySettings.selectAll().where { AccessibilitySettings.userId eq userId }.firstOrNull()
        AccessibilitySetting(result!![AccessibilitySettings.id], result[AccessibilitySettings.userId], result[AccessibilitySettings.fontSizeLevel], result[AccessibilitySettings.isHighContrast])
    }

    suspend fun getAccessibility(userId: Int) = dbExec {
        AccessibilitySettings.selectAll().where { AccessibilitySettings.userId eq userId }
            .map { AccessibilitySetting(it[AccessibilitySettings.id], it[AccessibilitySettings.userId], it[AccessibilitySettings.fontSizeLevel], it[AccessibilitySettings.isHighContrast]) }
            .firstOrNull()
    }

    suspend fun getAllZones() = dbExec {
        Zones.selectAll()
            .map { Zone(it[Zones.id], it[Zones.zoneName], it[Zones.floorNumber], it[Zones.congestionLevel]) }
    }

    suspend fun getZone(id: Int) = dbExec {
        Zones.selectAll().where { Zones.id eq id }
            .map { Zone(it[Zones.id], it[Zones.zoneName], it[Zones.floorNumber], it[Zones.congestionLevel]) }
            .firstOrNull()
    }

    suspend fun getAllShelves() = dbExec {
        Shelves.selectAll()
            .map { Shelf(it[Shelves.id], it[Shelves.zoneId], it[Shelves.shelfCode], it[Shelves.isLowAccessible]) }
    }

    suspend fun getShelf(id: Int) = dbExec {
        Shelves.selectAll().where { Shelves.id eq id }
            .map { Shelf(it[Shelves.id], it[Shelves.zoneId], it[Shelves.shelfCode], it[Shelves.isLowAccessible]) }
            .firstOrNull()
    }

    suspend fun getShelvesByZone(zoneId: Int) = dbExec {
        Shelves.selectAll().where { Shelves.zoneId eq zoneId }
            .map { Shelf(it[Shelves.id], it[Shelves.zoneId], it[Shelves.shelfCode], it[Shelves.isLowAccessible]) }
    }

    suspend fun getLowAccessibleShelves() = dbExec {
        Shelves.selectAll().where { Shelves.isLowAccessible eq true }
            .map { Shelf(it[Shelves.id], it[Shelves.zoneId], it[Shelves.shelfCode], it[Shelves.isLowAccessible]) }
    }

    suspend fun createServiceRequest(userId: Int, requestType: String): ServiceRequest {
        var id = 0
        dbExec {
            id = (ServiceRequests.insert {
                it[ServiceRequests.userId] = userId
                it[ServiceRequests.requestType] = requestType
                it[ServiceRequests.status] = "pending"
                it[ServiceRequests.createdAt] = System.currentTimeMillis()
            } get ServiceRequests.id)
        }
        return ServiceRequest(id, userId, "pending", requestType, System.currentTimeMillis())
    }

    suspend fun getServiceRequest(id: Int) = dbExec {
        ServiceRequests.selectAll().where { ServiceRequests.id eq id }
            .map { ServiceRequest(it[ServiceRequests.id], it[ServiceRequests.userId], it[ServiceRequests.status], it[ServiceRequests.requestType], it[ServiceRequests.createdAt]) }
            .firstOrNull()
    }

    suspend fun updateServiceRequestStatus(requestId: Int, status: String): ServiceRequest? = dbExec {
        ServiceRequests.update({ ServiceRequests.id eq requestId }) {
            it[ServiceRequests.status] = status
        }
        val result = ServiceRequests.selectAll().where { ServiceRequests.id eq requestId }.firstOrNull()
        if (result != null) {
            ServiceRequest(result[ServiceRequests.id], result[ServiceRequests.userId], result[ServiceRequests.status], result[ServiceRequests.requestType], result[ServiceRequests.createdAt])
        } else {
            null
        }
    }

    suspend fun getUserServiceRequests(userId: Int) = dbExec {
        ServiceRequests.selectAll().where { ServiceRequests.userId eq userId }
            .map { ServiceRequest(it[ServiceRequests.id], it[ServiceRequests.userId], it[ServiceRequests.status], it[ServiceRequests.requestType], it[ServiceRequests.createdAt]) }
    }
}