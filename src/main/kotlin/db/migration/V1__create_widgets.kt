package db.migration

import model.Users
import model.Books
import model.Loans
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class V1__create_widgets: BaseJavaMigration() {
    override fun migrate(context: Context?) {
        transaction {
            SchemaUtils.create(Users, Books, Loans)

            Users.insert { it[username] = "alice"; it[email] = "alice@test.com" }
            Users.insert { it[username] = "bob"; it[email] = "bob@test.com" }

            Books.insert { it[title] = "Kotlin in Action"; it[author] = "Dmitry Jemerov"; it[status] = "available" }
            Books.insert { it[title] = "Ktor Guide"; it[author] = "Ryan Harrison"; it[status] = "available" }
            Books.insert { it[title] = "Clean Code"; it[author] = "Robert Martin"; it[status] = "available" }
        }
    }
}