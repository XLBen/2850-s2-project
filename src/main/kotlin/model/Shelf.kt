package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object Shelves : Table() {
    val id = integer("id").autoIncrement()
    val zoneId = integer("zone_id")
    val shelfCode = varchar("shelf_code", 50)
    val isLowAccessible = bool("is_low_accessible").default(true)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Shelf(
    val id: Int,
    val zoneId: Int,
    val shelfCode: String,
    val isLowAccessible: Boolean
)