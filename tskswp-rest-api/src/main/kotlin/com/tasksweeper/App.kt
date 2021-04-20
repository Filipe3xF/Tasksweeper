package com.tasksweeper

import com.tasksweeper.exceptions.AuthenticationException
import com.tasksweeper.exceptions.AuthorizationException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    installContentNegotiation()
    installCallLogging()
    installCORS()
    installDefaultHeaders()
    installExceptionHandling()

    routing {
        get("/") {
            call.respond(mapOf("message" to "Hello, World!"))
        }
    }
}

fun Application.installExceptionHandling() = install(StatusPages) {
    exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized)
    }
    exception<AuthorizationException> { cause ->
        call.respond(HttpStatusCode.Forbidden)
    }
}

fun Application.installAuthentication() = install(Authentication) {
    TODO("Authentication yet to be done after the model creation.")
}

fun Application.installContentNegotiation() = install(ContentNegotiation) { gson {} }

fun Application.installCallLogging() = install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
}

fun Application.installCORS() = install(CORS) {
    method(HttpMethod.Options)
    method(HttpMethod.Get)
    method(HttpMethod.Post)
    method(HttpMethod.Put)
    method(HttpMethod.Delete)
    method(HttpMethod.Patch)
    header(HttpHeaders.Authorization)
    allowCredentials = true
    anyHost()
}

fun Application.installDefaultHeaders() = install(DefaultHeaders) {
    header("X-Engine", "Ktor")
}
