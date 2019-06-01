package com.spyrdonapps

import com.spyrdonapps.webapp.about
import com.spyrdonapps.webapp.home
import io.ktor.application.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    routing {
        home()
        about()
    }
}