package com.tasksweeper.controller

import com.tasksweeper.authentication.getUsername
import com.tasksweeper.service.AccountConsumableService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.accountConsumableController() {
    val accountConsumableService: AccountConsumableService by inject()

    authenticate {
        get("account/consumables") {
            accountConsumableService.getAccountConsumables(call.getUsername())
        }
    }
}