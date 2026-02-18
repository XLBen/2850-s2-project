package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object Loans : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val bookId = integer("book_id")
    val borrowDate = long("borrow_date")
    val returnDate = long("return_date").nullable()
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Loan(val id: Int, val userId: Int, val bookId: Int, val borrowDate: Long, val returnDate: Long? = null)