package com.spyrdonapps.repository

import com.spyrdonapps.model.EmojiPhrase
import com.spyrdonapps.model.EmojiPhrasesTable
import com.spyrdonapps.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException

class EmojiPhrasesRepository : Repository {

    override suspend fun add(emojiValue: String, phraseValue: String) {
        transaction {
            EmojiPhrasesTable.insert {
                it[emoji] = emojiValue
                it[phrase] = phraseValue
            }
        }
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

    private fun toEmojiPhrase(row: ResultRow): EmojiPhrase =
        EmojiPhrase(
            id = row[EmojiPhrasesTable.id].value,
            emoji = row[EmojiPhrasesTable.emoji],
            phrase = row[EmojiPhrasesTable.phrase]
        )
}