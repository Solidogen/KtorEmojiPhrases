package com.spyrdonapps.api

import com.spyrdonapps.API_VERSION
import com.spyrdonapps.model.Request
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES_API_ENDPOINT = "$API_VERSION/phrases"

@Location(PHRASES_API_ENDPOINT)
class PhrasesApi

fun Route.phrasesApi(db: Repository) {

    authenticate("jwt") {
        get<PhrasesApi> {
            call.respond(db.phrases())
        }

//        post<PhrasesApi> {
//            val request = call.receive<Request>()
//            val phrase = db.add("", request.emoji, request.phrase)
//            call.respond(phrase)
//        }
    }
}