package model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.Table

object AccessibilitySettings : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val fontSizeLevel = integer("font_size_level").default(1)  // 1=normal, 2=medium, 3=large
    val isHighContrast = bool("is_high_contrast").default(false)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class AccessibilitySetting(
    val id: Int,
    val userId: Int,
    val fontSizeLevel: Int,
    val isHighContrast: Boolean
)