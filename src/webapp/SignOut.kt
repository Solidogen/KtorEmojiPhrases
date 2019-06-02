package com.spyrdonapps.webapp

import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.redirect
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class SignOut : AppLocation

fun Route.signOut() {
    get<SignOut>{
        call.redirect(SignIn())
    }
}