package com.tasksweeper.controller

import com.tasksweeper.authentication.getUsername
import com.tasksweeper.exceptions.InvalidTaskIdException
import com.tasksweeper.service.TaskService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject


fun Routing.taskController() {
    val taskService: TaskService by inject()

    authenticate {
        post("/task") {
            call.receive<TaskInfoDTO>().let {
                taskService.createTask(
                    it.name, it.dueDate, it.dueTime, it.difficultyName,
                    it.repetition, call.getUsername(), it.description
                ).let { task ->
                    call.respond(
                        HttpStatusCode.Created,
                        task
                    )
                }
            }
        }

        delete("/task/{taskId}/success") {
            taskService.closeTaskSuccessfully(
                call.getUsername(),
                call.parameters["taskId"]!!.let { it.toLongOrNull() ?: throw InvalidTaskIdException(it) }
            ).let {
                call.respond(
                    HttpStatusCode.OK,
                    it
                )
            }
        }
    }
}

data class TaskInfoDTO(
    val name: String,
    val dueDate: DateDTO?,
    val dueTime: TimeDTO?,
    val difficultyName: String,
    val repetition: String?,
    val description: String?
)

data class DateDTO(val year: String, val month: String, val day: String)
data class TimeDTO(val hour: String, val minute: String, val second: String)
