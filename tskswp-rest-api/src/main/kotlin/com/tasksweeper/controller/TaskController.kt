package com.tasksweeper.controller

import com.tasksweeper.authentication.JWT
import com.tasksweeper.authentication.getUsername
import com.tasksweeper.service.TaskService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant


fun Routing.taskController() {
    val jwt: JWT by inject()
    val taskService: TaskService by inject()

    authenticate {
        post("/task") {
            call.receive<TaskInfoDTO>().let {
                val username = call.getUsername()
                taskService.createTask(
                    it.name, Instant.now(), it.dueDate, it.difficultyName,
                    it.repetition, username, it.description
                ).let {
                    call.respond(
                        HttpStatusCode.Created,
                        it
                    )
                }
            }
        }
    }
}


data class TaskInfoDTO(
    val name: String,
    val dueDate: String?,
    val difficultyName: String,
    val repetition: String?,
    val description: String?
)