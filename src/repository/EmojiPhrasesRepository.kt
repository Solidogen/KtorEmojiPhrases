package com.spyrdonapps.repository

import com.spyrdonapps.model.EmojiPhrase
import com.spyrdonapps.model.EmojiPhrasesTable
import com.spyrdonapps.model.User
import com.spyrdonapps.model.UsersTable
import com.spyrdonapps.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException

class EmojiPhrasesRepository : Repository {

    override suspend fun add(userId: String, emojiValue: String, phraseValue: String): EmojiPhrase? =
        dbQuery {
            EmojiPhrasesTable.insert {
                it[user] = userId
                it[emoji] = emojiValue
                it[phrase] = phraseValue
            }.resultedValues?.map { toEmojiPhrase(it) }?.get(0)
        }

    override suspend fun phrase(id: Int): EmojiPhrase? = dbQuery {
        EmojiPhrasesTable.select {
            EmojiPhrasesTable.id eq id
        }.mapNotNull {
            toEmojiPhrase(it)
        }.singleOrNull()
    }

    override suspend fun phrase(id: String): EmojiPhrase? = phrase(id.toInt())

    override suspend fun phrases(): List<EmojiPhrase> = dbQuery {
        EmojiPhrasesTable.selectAll().map { toEmojiPhrase(it) }
    }

    override suspend fun remove(id: Int): Boolean {
        if (phrase(id) == null) {
            throw IllegalArgumentException("No phrase found for id $id.")
        }
        return dbQuery {
            EmojiPhrasesTable.deleteWhere { EmojiPhrasesTable.id eq id } > 0
        }
    }

    override suspend fun remove(id: String): Boolean = remove(id.toInt())

    override suspend fun clear() {
        EmojiPhrasesTable.deleteAll()
    }

    override suspend fun user(userId: String, hash: String?): User? {
        val user = dbQuery {
            UsersTable.select {
                UsersTable.id eq userId
            }.mapNotNull {
                toUser(it)
            }.singleOrNull()
        }
        return when {
            user == null -> null
            hash == null -> user
            user.passwordHash == hash -> user
            else -> null
        }
    }

    override suspend fun userByEmail(email: String): User? = dbQuery {
        UsersTable.select {
            UsersTable.email eq email
        }.mapNotNull {
            toUser(it)
        }.singleOrNull()
    }

    override suspend fun userById(userId: String) = dbQuery {
        UsersTable.select {
            UsersTable.id eq userId
        }.mapNotNull {
            toUser(it)
        }.singleOrNull()
    }

    override suspend fun createUser(user: User) {
        dbQuery {
            UsersTable.insert {
                it[id] = user.userId
                it[displayName] = user.displayName
                it[email] = user.email
                it[passwordHash] = user.passwordHash
            }
        }
    }

    private fun toEmojiPhrase(row: ResultRow): EmojiPhrase =
        EmojiPhrase(
            id = row[EmojiPhrasesTable.id].value,
            userId = row[EmojiPhrasesTable.user],
            emoji = row[EmojiPhrasesTable.emoji],
            phrase = row[EmojiPhrasesTable.phrase]
        )

    private fun toUser(row: ResultRow): User =
        User(
            userId = row[UsersTable.id],
            email = row[UsersTable.email],
            displayName = row[UsersTable.displayName],
            passwordHash = row[UsersTable.passwordHash]
        )
}