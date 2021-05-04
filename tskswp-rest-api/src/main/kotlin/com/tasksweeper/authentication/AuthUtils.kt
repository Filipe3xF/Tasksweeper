package com.tasksweeper.authentication

import io.ktor.application.*
import io.ktor.auth.*

fun ApplicationCall.getUsername() = this.authentication.principal<UserIdPrincipal>()!!.name
