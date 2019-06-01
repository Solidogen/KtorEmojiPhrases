package com.spyrdonapps.webapp

import com.spyrdonapps.model.User
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "/phrases"

fun Route.phrases(db: Repository) {

    authenticate("auth") {
        get(PHRASES) {
            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl",
                    mapOf(
                        "phrases" to phrases,
                        "displayName" to user.displayName
                    )
                )
            )
        }
    }
}