package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val HOME = "/"

@Location(HOME)
class Home : AppLocation

fun Route.home() {
    get<Home> {
        call.respond(FreeMarkerContent("home.ftl", null))
    }
}