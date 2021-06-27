package com.tasksweeper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.tasksweeper.authentication.JWT
import com.tasksweeper.controller.accountController
import com.tasksweeper.controller.apiController
import com.tasksweeper.controller.consumableController
import com.tasksweeper.controller.taskController
import com.tasksweeper.exceptions.*
import com.tasksweeper.repository.*
import com.tasksweeper.service.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import java.time.format.DateTimeParseException


val serviceModule = module {
    single { AccountService() }
    single { AccountStatusService() }
    single { TaskService() }
    single { ConsumableService() }
    single { AccountConsumableService() }
}

val repositoryModule = module {
    single { AccountRepository() }
    single { AccountStatusRepository() }
    single { TaskRepository() }
    single { ConsumableRepository() }
    single { AccountConsumableRepository() }
}

val appModule = module {
    single { JWT() }
}

fun main(args: Array<String>) {
    startKoin {
        slf4jLogger()
        modules(appModule, serviceModule, repositoryModule)
    }

    DatabaseFactory.init()

    EngineMain.main(args)
}

fun Application.module() {

    installContentNegotiation()
    installAuthentication()
    installCallLogging()
    installCORS()
    installDefaultHeaders()
    installExceptionHandling()


    routing {
        get("/") {
            call.respond(mapOf("message" to "Hello, World!"))
        }

        accountController()
        apiController()
        taskController()
        consumableController()
    }
}

fun Application.installExceptionHandling() = install(StatusPages) {
    exception<ExposedSQLException> {
        call.respond(HttpStatusCode.Conflict, AppError(it.message!!.substringAfter("Detail: ")))
    }
    exception<DatabaseNotFoundException> {
        call.respond(HttpStatusCode.NotFound, AppError(it.message!!))
    }
    exception<InvalidCredentialsException> {
        call.respond(HttpStatusCode.Unauthorized, AppError(it.message ?: "Error due to invalid credentials."))
    }
    exception<InvalidUsernameException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<InvalidEmailException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<InvalidRepetitionException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<InvalidDifficultyException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<InvalidDueDateException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<DateTimeParseException> {
        call.respond(HttpStatusCode.BadRequest, AppError("${it.parsedString} is not a valid timestamp."))
    }
    exception<InvalidTaskIdException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<NotAuthorizedTaskDeletionException> {
        call.respond(HttpStatusCode.Forbidden, AppError(it.message!!))
    }
    exception<NotEnoughGoldException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<InvalidConsumableIdException>{
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
    }
    exception<NotAuthorizedTaskCompletionException> {
        call.respond(HttpStatusCode.Forbidden, AppError(it.message!!))
    }
    exception<TaskAlreadyClosedException> {
        call.respond(HttpStatusCode.BadRequest, AppError(it.message!!))
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
        loadKoinModules(module {
            single { this@jackson }
        })
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
