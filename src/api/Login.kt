package com.spyrdonapps.api

import com.spyrdonapps.auth.JwtService
import com.spyrdonapps.auth.hash
import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.redirect
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val LOGIN_ENDPOINT = "/login"

@Location(LOGIN_ENDPOINT)
class Login : AppLocation

fun Route.login(db: Repository, jwtService: JwtService) {

    post<Login> {
        val params = call.receiveParameters()
        val userId = params["userId"] ?: return@post call.redirect(it)
        val password = params["password"] ?: return@post call.redirect(it)

        val user = db.user(userId, hash(password))
        user?.let {
            val token = jwtService.generateToken(user)
            call.respondText(token)
        } ?: run {
            call.respondText("Invalid user")
        }
    }
}