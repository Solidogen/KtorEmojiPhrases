package com.spyrdonapps

import com.ryanharter.ktor.moshi.moshi
import com.spyrdonapps.api.phrase
import com.spyrdonapps.auth.hash
import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.User
import com.spyrdonapps.repository.DatabaseFactory
import com.spyrdonapps.repository.EmojiPhrasesRepository
import com.spyrdonapps.webapp.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(
                e.localizedMessage,
                ContentType.Text.Plain, HttpStatusCode.InternalServerError
            )
        }
    }

    install(ContentNegotiation) {
        moshi()
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Locations)

    val hashFunction = { s: String ->
        hash(s)
    }

    DatabaseFactory.init()

    val db = EmojiPhrasesRepository()

    routing {
        static("/static") {
            resources("images")
        }
        home()
        about()
        phrases(db)
        signIn(db, hashFunction)
        signOut()
        signUp(db, hashFunction)

        // API
        phrase(db)
    }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: AppLocation) {
    respondRedirect(application.locations.href(location))
}