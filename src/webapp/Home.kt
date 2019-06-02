package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.EPSession
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val HOME = "/"

@Location(HOME)
class Home : AppLocation

fun Route.home(db: Repository) {
    get<Home> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
        call.respond(FreeMarkerContent("home.ftl", mapOf(
            "user" to user
        )))
    }
}