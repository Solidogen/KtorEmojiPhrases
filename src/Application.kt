package com.spyrdonapps

import com.ryanharter.ktor.moshi.moshi
import com.spyrdonapps.api.phrase
import com.spyrdonapps.model.AppLocation
import com.spyrdonapps.model.User
import com.spyrdonapps.repository.InMemoryRepository
import com.spyrdonapps.webapp.about
import com.spyrdonapps.webapp.home
import com.spyrdonapps.webapp.phrases
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

    install(Authentication) {
        basic(name = "auth") {
            realm = "Ktor server"
            validate { credentials ->
                if (credentials.password == "${credentials.name}123") User(credentials.name) else null
            }
        }
    }

    install(Locations)

    val db = InMemoryRepository()

    routing {
        static("/static") {
            resources("images")
        }
        home()
        about()
        phrases(db)

        // API
        phrase(db)
    }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: AppLocation) {
    respondRedirect(application.locations.href(location))
}