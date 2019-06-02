package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

const val SIGNUP = "/signup"

@Location(SIGNUP)
data class SignUp(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = "") : AppLocation

fun Route.signUp(db: Repository, hashFunction: (String) -> String) {
    get<SignUp>{
        call.respond(FreeMarkerContent("signup.ftl", null))
    }
}