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

            val shelf1 = Shelves.insert { it[zoneId] = zone1; it[shelfCode] = "A1"; it[isLowAccessible] = true } get Shelves.id
            val shelf2 = Shelves.insert { it[zoneId] = zone1; it[shelfCode] = "A2"; it[isLowAccessible] = true } get Shelves.id
            val shelf3 = Shelves.insert { it[zoneId] = zone2; it[shelfCode] = "B1"; it[isLowAccessible] = false } get Shelves.id
            val shelf4 = Shelves.insert { it[zoneId] = zone2; it[shelfCode] = "B2"; it[isLowAccessible] = false } get Shelves.id

            Books.insert { it[title] = "Kotlin in Action"; it[author] = "Dmitry Jemerov"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "Ktor Guide"; it[author] = "Ryan Harrison"; it[status] = "available"; it[shelfId] = shelf2 }
            Books.insert { it[title] = "Clean Code"; it[author] = "Robert Martin"; it[status] = "available"; it[shelfId] = shelf3 }
            Books.insert { it[title] = "The Great Gatsby"; it[author] = "F. Scott Fitzgerald"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "To Kill a Mockingbird"; it[author] = "Harper Lee"; it[status] = "borrowed"; it[shelfId] = shelf2 }
            Books.insert { it[title] = "1984"; it[author] = "George Orwell"; it[status] = "available"; it[shelfId] = shelf4 }
            Books.insert { it[title] = "Pride and Prejudice"; it[author] = "Jane Austen"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "The Hobbit"; it[author] = "J.R.R. Tolkien"; it[status] = "available"; it[shelfId] = shelf3 }
            Books.insert { it[title] = "Dune"; it[author] = "Frank Herbert"; it[status] = "available"; it[shelfId] = shelf2 }
            Books.insert { it[title] = "Foundation"; it[author] = "Isaac Asimov"; it[status] = "available"; it[shelfId] = shelf4 }
            Books.insert { it[title] = "Harry Potter and the Sorcerer's Stone"; it[author] = "J.K. Rowling"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "The Lord of the Rings"; it[author] = "J.R.R. Tolkien"; it[status] = "available"; it[shelfId] = shelf2 }
            Books.insert { it[title] = "Brave New World"; it[author] = "Aldous Huxley"; it[status] = "available"; it[shelfId] = shelf4 }
            Books.insert { it[title] = "The Catcher in the Rye"; it[author] = "J.D. Salinger"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "Jane Eyre"; it[author] = "Charlotte Bronte"; it[status] = "available"; it[shelfId] = shelf3 }
            Books.insert { it[title] = "Wuthering Heights"; it[author] = "Emily Bronte"; it[status] = "available"; it[shelfId] = shelf2 }
            Books.insert { it[title] = "The Handmaid's Tale"; it[author] = "Margaret Atwood"; it[status] = "available"; it[shelfId] = shelf4 }
            Books.insert { it[title] = "Sapiens"; it[author] = "Yuval Noah Harari"; it[status] = "available"; it[shelfId] = shelf1 }
            Books.insert { it[title] = "Thinking, Fast and Slow"; it[author] = "Daniel Kahneman"; it[status] = "available"; it[shelfId] = shelf3 }
            Books.insert { it[title] = "Atomic Habits"; it[author] = "James Clear"; it[status] = "available"; it[shelfId] = shelf2 }

            AccessibilitySettings.insert { it[userId] = user1; it[fontSizeLevel] = 1; it[isHighContrast] = false }
            AccessibilitySettings.insert { it[userId] = user2; it[fontSizeLevel] = 1; it[isHighContrast] = false }
        }
    }
}