package com.tasksweeper.controller

import com.tasksweeper.authentication.getUsername
import com.tasksweeper.exceptions.InvalidConsumableIdException
import com.tasksweeper.service.ConsumableService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Routing.consumableController() {
    val consumableService: ConsumableService by inject()
    authenticate {
        post("/consumable/{consumableId}/buy") {
            consumableService.obtainItem(
                call.getUsername(),
                call.parameters["consumableId"]!!.let { it.toLongOrNull() ?: throw InvalidConsumableIdException(it)}
            ).let { consumable ->
                call.respond(
                    HttpStatusCode.OK,
                    consumable
                )
            }
        }
    }
}
