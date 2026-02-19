package db.migration

import model.Users
import model.Books
import model.Loans
import model.AccessibilitySettings
import model.Zones
import model.Shelves
import model.ServiceRequests
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class V1__create_widgets: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            
            SchemaUtils.create(
                Users, 
                Books, 
                Loans,
                AccessibilitySettings,
                Zones,
                Shelves,
                ServiceRequests
            )

            
            val user1 = Users.insert { it[username] = "alice"; it[email] = "alice@test.com" } get Users.id
            val user2 = Users.insert { it[username] = "bob"; it[email] = "bob@test.com" } get Users.id

            
            Books.insert { it[title] = "Kotlin in Action"; it[author] = "Dmitry Jemerov"; it[status] = "available" }
            Books.insert { it[title] = "Ktor Guide"; it[author] = "Ryan Harrison"; it[status] = "available" }
            Books.insert { it[title] = "Clean Code"; it[author] = "Robert Martin"; it[status] = "available" }

          
            AccessibilitySettings.insert { it[userId] = user1; it[fontSizeLevel] = 1; it[isHighContrast] = false }
            AccessibilitySettings.insert { it[userId] = user2; it[fontSizeLevel] = 1; it[isHighContrast] = false }

           
            val zone1 = Zones.insert { 
                it[zoneName] = "A Zone"; 
                it[floorNumber] = 1; 
                it[congestionLevel] = "low" 
            } get Zones.id
            
            val zone2 = Zones.insert { 
                it[zoneName] = "B Zone"; 
                it[floorNumber] = 1; 
                it[congestionLevel] = "medium" 
            } get Zones.id

          
            Shelves.insert { it[zoneId] = zone1; it[shelfCode] = "A1"; it[isLowAccessible] = true }
            Shelves.insert { it[zoneId] = zone1; it[shelfCode] = "A2"; it[isLowAccessible] = true }
            Shelves.insert { it[zoneId] = zone2; it[shelfCode] = "B1"; it[isLowAccessible] = false }
        }
    }
}