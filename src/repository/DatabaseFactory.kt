package com.spyrdonapps.repository

import com.spyrdonapps.model.EmojiPhrasesTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(EmojiPhrasesTable)

            EmojiPhrasesTable.insert {
                it[emoji] = "e1"
                it[phrase] = "p1"
            }
            EmojiPhrasesTable.insert {
                it[emoji] = "e2"
                it[phrase] = "p2"
            }
        }
    }

    private fun hikari() = HikariDataSource(HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = System.getenv("JDBC_DATABASE_URL")
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }
}