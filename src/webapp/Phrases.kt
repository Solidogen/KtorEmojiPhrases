package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.EPSession
import com.spyrdonapps.model.User
import com.spyrdonapps.redirect
import com.spyrdonapps.repository.Repository
import com.spyrdonapps.securityCode
import com.spyrdonapps.verifyCode
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val PHRASES = "/phrases"

@Location(PHRASES)
class Phrases : AppLocation

fun Route.phrases(db: Repository, hashFunction: (String) -> String) {

    get<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        user?.run {
            val phrases = db.phrases()
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl",
                    mapOf(
                        "phrases" to phrases,
                        "user" to user,
                        "date" to date,
                        "code" to code
                    ),
                    userId
                )
            )
        } ?: run {
            call.redirect(SignIn())
        }
    }

    post<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }

        val params = call.receiveParameters()
        val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = params["code"] ?: return@post call.redirect(it)
        val action = params["action"] ?: throw IllegalArgumentException("Missing parameter: action")

        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(SignIn())
        }

        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                db.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
                db.add(user!!.userId, emoji, phrase)
            }
        }
        call.redirect(Phrases())
    }
}