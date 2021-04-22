package com.tasksweeper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.tasksweeper.authentication.JWT
import com.tasksweeper.controller.accountController
import com.tasksweeper.exceptions.AuthenticationException
import com.tasksweeper.exceptions.AuthorizationException
import com.tasksweeper.repository.DatabaseFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val appModule = module {
    single { JWT() }
}

fun Application.module() {

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    installContentNegotiation()
    installAuthentication()
    installCallLogging()
    installCORS()
    installDefaultHeaders()
    installExceptionHandling()


    DatabaseFactory.init()

    routing {
        get("/") {
            call.respond(mapOf("message" to "Hello, World!"))
        }

        accountController()
    }
}

fun Application.installExceptionHandling() = install(StatusPages) {
    exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized)
    }
    exception<AuthorizationException> { cause ->
        call.respond(HttpStatusCode.Forbidden)
    }
    exception<ExposedSQLException> { cause ->
        call.respond(HttpStatusCode.Conflict, mapOf("error" to cause.message))
    }
    exception<NoSuchElementException> { cause ->
        call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
    }
}

fun Application.installAuthentication() = install(Authentication) {
    jwt {
        val jwt: JWT by inject()
        verifier(jwt.verifier)
        validate {
            UserIdPrincipal(it.payload.getClaim("username").asString())
        }
    }
}

fun Application.installContentNegotiation() = install(ContentNegotiation) {
    jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)

        loadKoinModules(
            module {
                single { this@jackson }
            }
        )
    }
}

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
