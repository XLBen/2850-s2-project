package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100)
    val email = varchar("email", 100)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(val id: Int, val username: String, val email: String)