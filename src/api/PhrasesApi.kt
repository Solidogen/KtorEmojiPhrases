package com.spyrdonapps.api

import com.spyrdonapps.API_VERSION
import com.spyrdonapps.api.requests.PhrasesApiRequest
import com.spyrdonapps.apiUser
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
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

        post<PhrasesApi> {
            val user = call.apiUser ?: throw IllegalArgumentException("User was null")

            try {
                val request = call.receive<PhrasesApiRequest>()
                db.add(user.userId, request.emoji, request.phrase)?.let { phrase ->
                    call.respond(phrase)
                } ?: run {
                    call.respondText("Invalid data received", status = HttpStatusCode.InternalServerError)
                }
            } catch (e: Throwable) {
                call.respondText("Invalid data received", status = HttpStatusCode.BadRequest)
            }
        }
    }
}