package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val SIGNIN = "/signin"

@Location(SIGNIN)
data class SignIn (val userId: String = "", val error: String = "") : AppLocation

fun Route.signIn(db: Repository, hashFunction: (String) -> String) {
    get<SignIn>{
        call.respond(FreeMarkerContent("signin.ftl", null))
    }
}