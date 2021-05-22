package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.AppError
import com.tasksweeper.module
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.service.TaskService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.instantOf
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class TaskControllerTest : KoinTest {
    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { TaskService() }
                single { mockk<TaskRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `Insert a Task successfully with the correct parameters`() {
        val taskInfoDto = TaskInfoDTO(
            "someTask",
            DateDTO("2021", "06", "14"),
            TimeDTO("23", "59", "59"),
            "Medium",
            "Weekly",
            "Just a test Task"
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.insertTask(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns TaskDTO(
            1,
            taskInfoDto.name,
            mockk(relaxed = true),
            mockk(relaxed = true),
            taskInfoDto.difficultyName,
            taskInfoDto.repetition,
            "username",
            taskInfoDto.description
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.Created
                response.content shouldContain taskInfoDto.name
                response.content shouldContain taskInfoDto.description!!
                response.content shouldContain taskInfoDto.difficultyName
                response.content shouldContain taskInfoDto.repetition!!
                response.content shouldContain "username"
            }
        }
    }

    @Test
    fun `Insert a Task unsuccessfully for inserting incorrect difficulty`() {
        val taskInfoDto = TaskInfoDTO(
            "someTask",
            DateDTO("2021", "06", "14"),
            TimeDTO("23", "59", "59"),
            "I don't know",
            "Weekly",
            "Just a test Task"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain taskInfoDto.difficultyName
            }
        }
    }

    @Test
    fun `Insert a Task unsuccessfully for inserting incorrect repetition`() {
        val taskInfoDto = TaskInfoDTO(
            "someTask",
            DateDTO("2021", "06", "14"),
            TimeDTO("23", "59", "59"),
            "Medium",
            "sometimes",
            "Just a test Task"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain taskInfoDto.repetition!!
            }
        }
    }

    @Test
    fun `Insert a Task unsuccessfully with the a due date in the past`() {
        val taskInfoDto = TaskInfoDTO(
            "someTask",
            DateDTO("0420", "06", "14"),
            TimeDTO("23", "59", "59"),
            "Medium",
            "Weekly",
            "Just a test Task"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain instantOf(taskInfoDto.dueDate!!, taskInfoDto.dueTime!!).toString()
            }
        }
    }

    @Test
    fun `Get blocked out for not having the credentials`() {
        val taskInfoDto = TaskInfoDTO(
            "someTask",
            DateDTO("2021", "06", "14"),
            TimeDTO("23", "59", "59"),
            "Medium",
            "Weekly",
            "Just a test Task"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }
}
