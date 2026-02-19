package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object Zones : Table() {
    val id = integer("id").autoIncrement()
    val zoneName = varchar("zone_name", 100)
    val floorNumber = integer("floor_number")
    val congestionLevel = varchar("congestion_level", 20).default("low")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Zone(
    val id: Int,
    val zoneName: String,
    val floorNumber: Int,
    val congestionLevel: String
)