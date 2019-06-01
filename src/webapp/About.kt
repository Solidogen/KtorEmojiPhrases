package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val ABOUT = "/about"

@Location(ABOUT)
class About : AppLocation

fun Route.about() {
    get<About> {
        call.respond(FreeMarkerContent("about.ftl", null))
    }
}