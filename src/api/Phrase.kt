package com.spyrdonapps.api

import com.spyrdonapps.API_VERSION
import com.spyrdonapps.model.Request
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"

@Location(PHRASE_ENDPOINT)
class Phrase

fun Route.phrase(db: Repository) {

    post<Phrase> {
        val request = call.receive<Request>()
        val phrase = db.add("", request.emoji, request.phrase)
        call.respond(phrase)
    }
}