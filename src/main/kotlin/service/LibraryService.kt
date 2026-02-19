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

    suspend fun createBook(title: String, author: String): Book {
        var id = 0
        dbExec {
            id = (Books.insert {
                it[Books.title] = title
                it[Books.author] = author
                it[Books.status] = "available"
            } get Books.id)
        }
        return Book(id, title, author, "available")
    }

    suspend fun getAllBooks() = dbExec { 
        Books.selectAll().map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status]) } 
    }

    suspend fun searchBooks(keyword: String) = dbExec {
        Books.selectAll()
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status]) }
            .filter { it.title.contains(keyword, ignoreCase = true) || it.author.contains(keyword, ignoreCase = true) }
    }

    suspend fun searchBooksLowAccessible(keyword: String) = dbExec {
        val books = Books.selectAll()
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status]) }
            .filter { it.title.contains(keyword, ignoreCase = true) || it.author.contains(keyword, ignoreCase = true) }
        
        val lowAccessibleShelfIds = Shelves.selectAll()
            .where { Shelves.isLowAccessible eq true }
            .map { it[Shelves.id] }
        
        books
    }

    suspend fun getBook(id: Int) = dbExec {
        Books.selectAll().where { Books.id eq id }
            .map { Book(it[Books.id], it[Books.title], it[Books.author], it[Books.status]) }
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
}