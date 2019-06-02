package com.spyrdonapps.webapp

import com.spyrdonapps.auth.MIN_PASSWORD_LENGTH
import com.spyrdonapps.auth.MIN_USER_ID_LENGTH
import com.spyrdonapps.auth.userNameValid
import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.EPSession
import com.spyrdonapps.redirect
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val SIGNIN = "/signin"

@Location(SIGNIN)
data class SignIn(val userId: String = "", val error: String = "") : AppLocation

fun Route.signIn(db: Repository, hashFunction: (String) -> String) {
    post<SignIn> {
        val signUpParameters = call.receiveParameters()
        val userId = signUpParameters["userId"] ?: return@post call.redirect(it)
        val password = signUpParameters["password"] ?: return@post call.redirect(it)

        val signUpError = SignUp(userId)

        val signIn = when {
            userId.length < MIN_USER_ID_LENGTH -> null
            password.length < MIN_PASSWORD_LENGTH -> null
            !userNameValid(userId) -> null
            else -> db.user(userId, hashFunction(password))
        }

        signIn?.run {
            call.sessions.set(EPSession(signIn.userId))
            call.redirect(Phrases())
        } ?: run {
            call.redirect(signUpError.copy(error = "Invalid username or password"))
        }
    }

    get<SignIn> {
        val user = call.sessions.get<EPSession>()?.let {
            db.user(it.userId)
        }
        user?.run {
            call.respond(FreeMarkerContent("signin.ftl", null))
        } ?: run {
            call.redirect(Home())
        }
    }
}