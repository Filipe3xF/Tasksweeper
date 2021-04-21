package com.tasksweeper.controller

import com.tasksweeper.authentication.JWT
import com.tasksweeper.service.checkAccount
import com.tasksweeper.service.registerAccount
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.accountController() {
    val jwt: JWT by inject()

    post("/register") {
        call.receive<RegisterDTO>().let {
            registerAccount(it.username, it.email, it.password).let { account ->
                call.respond(
                    mapOf(
                        "jwt" to jwt.sign(account.username)
                    )
                )
            }
        }
    }

    post("/login") {
        call.receive<LoginDTO>().let {
            checkAccount(it.username, it.password).let { account ->
                call.respond(
                    mapOf(
                        "jwt" to jwt.sign(account.username)
                    )
                )
            }
        }
    }
}

data class LoginDTO(val username: String, val password: String)
data class RegisterDTO(val username: String, val email: String, val password: String)
