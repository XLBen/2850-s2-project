package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object Books : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val status = varchar("status", 20)
    val shelfId = integer("shelf_id").nullable()
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val status: String,
    val shelfId: Int? = null
)