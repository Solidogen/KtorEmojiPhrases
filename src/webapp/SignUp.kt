package com.spyrdonapps.webapp

import com.spyrdonapps.auth.MIN_PASSWORD_LENGTH
import com.spyrdonapps.auth.MIN_USER_ID_LENGTH
import com.spyrdonapps.auth.userNameValid
import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.EPSession
import com.spyrdonapps.model.User
import com.spyrdonapps.redirect
import com.spyrdonapps.repository.Repository
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val SIGNUP = "/signup"

@Location(SIGNUP)
data class SignUp(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
) : AppLocation

fun Route.signUp(db: Repository, hashFunction: (String) -> String) {
    post<SignUp> {
        val user = call.sessions.get<EPSession>()?.let {
            db.user(it.userId)
        }
        user?.run {
            return@post call.redirect(Phrases())
        }
        val signUpParameters = call.receiveParameters()
        val userId = signUpParameters["userId"] ?: return@post call.redirect(it)
        val password = signUpParameters["password"] ?: return@post call.redirect(it)
        val displayName = signUpParameters["displayName"] ?: return@post call.redirect(it)
        val email = signUpParameters["email"] ?: return@post call.redirect(it)

        val signUpError = SignUp(userId, displayName, email)

        when {
            password.length < MIN_PASSWORD_LENGTH ->
                call.redirect(signUpError.copy(error = "Password should be at least $MIN_PASSWORD_LENGTH characters long"))
            userId.length < MIN_USER_ID_LENGTH ->
                call.redirect(signUpError.copy(error = "Username should be at least $MIN_USER_ID_LENGTH characters long"))
            !userNameValid(userId) ->
                call.redirect(signUpError.copy(error = "Username should consist of digits, letters, dots or underscores"))
            db.user(userId) != null ->
                call.redirect(signUpError.copy(error = "User with the following username is already registered"))
            else -> {
                val hash = hashFunction(password)
                val newUser = User(userId, email, displayName, hash)

                try {
                    db.createUser(newUser)
                } catch (e: Throwable) {
                    when {
                        db.user(userId) != null ->
                            call.redirect(signUpError.copy(error = "User with the following username is already registered"))
                        db.userByEmail(email) != null ->
                            call.redirect(signUpError.copy(error = "User with the following email $email is already registered"))
                        else -> {
                            application.log.error("Failed to register user", e)
                            call.redirect(signUpError.copy(error = "Failed to register"))
                        }
                    }
                }

                call.sessions.set(EPSession(newUser.userId))
                call.redirect(Phrases())
            }
        }
    }

    get<SignUp> {
        val user = call.sessions.get<EPSession>()?.let {
            db.user(it.userId)
        }
        user?.run {
            call.redirect(Phrases())
        } ?: run {
            call.respond(FreeMarkerContent("signup.ftl", mapOf(
                "error" to it.error
            )))
        }
    }
}