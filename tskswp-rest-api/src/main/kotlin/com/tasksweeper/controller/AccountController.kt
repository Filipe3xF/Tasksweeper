package com.tasksweeper.controller

import com.tasksweeper.authentication.JWT
import com.tasksweeper.authentication.getUsername
import com.tasksweeper.service.AccountService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.accountController() {
    val startingLevel = 1
    val jwt: JWT by inject()
    val accountService: AccountService by inject()

    post("/register") {
        call.receive<RegisterDTO>().let {
            accountService.registerAccount(it.username, it.email, it.password, startingLevel).let { account ->
                call.respond(
                    HttpStatusCode.Created,
                    JwtDTO(
                        jwt.sign(account.username)
                    )
                )
            }
        }
    }

    post("/login") {
        call.receive<LoginDTO>().let {
            accountService.checkAccount(it.username, it.password).let { account ->
                call.respond(
                    JwtDTO(
                        jwt.sign(account.username)
                    )
                )
            }
        }
    }

    authenticate {
        get("/account") {
            call.respond(
                accountService.getAccount(call.getUsername())
            )
        }
    }
}

data class LoginDTO(val username: String, val password: String)
data class RegisterDTO(val username: String, val email: String, val password: String)
data class JwtDTO(val jwt: String)
