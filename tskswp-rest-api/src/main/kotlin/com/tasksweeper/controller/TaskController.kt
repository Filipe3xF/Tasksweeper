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
    val taskService: TaskService by inject()

    authenticate {
        post("/task") {
            call.receive<TaskInfoDTO>().let {
                taskService.createTask(
                    it.name, it.dueDate, it.dueTime, it.difficultyName,
                    it.repetition, call.getUsername(), it.description
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
    val dueDate: DateDTO?,
    val dueTime: TimeDTO?,
    val difficultyName: String,
    val repetition: String?,
    val description: String?
)

data class DateDTO(val year: String, val month: String, val day: String)
data class TimeDTO(val hour: String, val minute: String, val second: String)