package com.tasksweeper.controller

import com.tasksweeper.authentication.getUsername
import com.tasksweeper.exceptions.InvalidConsumableIdException
import com.tasksweeper.service.AccountConsumableService
import com.tasksweeper.service.ConsumableService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.accountConsumableController() {

    val accountConsumableService: AccountConsumableService by inject()

    authenticate {
        post("/account/consumable/{consumableId}") {
            accountConsumableService.useItem(
                call.getUsername(),
                call.parameters["consumableId"]!!.let { it.toLongOrNull() ?: throw InvalidConsumableIdException(it) }
            ).let {
                call.respond(it)
            }
        }

        get("account/consumables") {
            call.respond(
                accountConsumableService.getAccountConsumables(call.getUsername())
            )
        }
    }
}
