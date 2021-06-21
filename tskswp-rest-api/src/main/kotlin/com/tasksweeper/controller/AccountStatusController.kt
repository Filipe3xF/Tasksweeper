package com.tasksweeper.controller

import com.tasksweeper.authentication.getUsername
import com.tasksweeper.service.AccountStatusService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.accountStatusController() {
    val accountStatusService: AccountStatusService by inject()

    authenticate {
        get("/accountStatus") {
            call.respond(
                accountStatusService.getAccountStatus(call.getUsername())
            )
        }
    }
}

data class AccountStatusResponseDTO(
    val statusName: String,
    val value: Long,
    val maxValue: Long?
)