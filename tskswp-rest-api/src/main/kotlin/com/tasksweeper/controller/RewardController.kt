package com.tasksweeper.controller

import com.tasksweeper.authentication.JWT
import com.tasksweeper.authentication.getUsername
import com.tasksweeper.service.RewardService
import com.tasksweeper.service.TaskService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant

fun Routing.rewardController() {
    val rewardService: RewardService by inject()

    authenticate {
        post("/reward") {
            call.receive<RewardDTO>().let {
                rewardService.giveReward(
                    call.getUsername(),
                    it.taskId
                ).let {
                    call.respond(
                        HttpStatusCode.Accepted,
                        it
                    )
                }
            }
        }
    }
}

data class RewardDTO(
    val taskId: Long
)