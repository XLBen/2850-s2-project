package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object ServiceRequests : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val status = varchar("status", 20).default("pending")  // pending, in_progress, completed
    val requestType = varchar("request_type", 50)  // "find_book", "need_help"
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class ServiceRequest(
    val id: Int,
    val userId: Int,
    val status: String,
    val requestType: String,
    val createdAt: Long
)