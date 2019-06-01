package com.spyrdonapps.api

import com.spyrdonapps.API_VERSION
import com.spyrdonapps.model.EmojiPhrase
import com.spyrdonapps.model.Request
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"

fun Route.phrase(db: Repository) {
    post(PHRASE_ENDPOINT) {
        val request = call.receive<Request>()
        val phrase = db.add(EmojiPhrase(request.emoji, request.phrase))
        call.respond(phrase)
    }
}