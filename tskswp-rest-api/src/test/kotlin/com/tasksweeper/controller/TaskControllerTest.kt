package com.tasksweeper.controller

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.InvalidDifficultyException
import com.tasksweeper.exceptions.InvalidRepetitionException
import com.tasksweeper.module
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.service.AccountService
import com.tasksweeper.service.AccountStatusService
import com.tasksweeper.service.TaskService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class TaskControllerTest : KoinTest {
    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { TaskService() }
                single { mockk<TaskRepository>()}
                single { mockkStatic("java.time.Instant")}
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    val HOURS : Long = 23
    val MINUTES : Long = 59
    val SECONDS : Long = 59

    val formatter = DateTimeFormatterBuilder()
        .appendPattern("dd-MM-yyyy")
        .parseDefaulting(ChronoField.HOUR_OF_DAY, HOURS)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, MINUTES)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, SECONDS)
        .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
        .toFormatter()
        .withZone(ZoneId.systemDefault())



    /*
    Tests the correct insertion of the task should the parameters be correctly inserted (Pre-existing account, existing
    difficulty and repetition, due date being after starting date). However, it doesn't test dates very well since it's difficult to test
    the dates
     */
    @Test
    fun `Insert a Test sucessfully with the correct parameters`(){
        val current = formatter.parse("20-05-2021", Instant :: from)
        coEvery {
            Instant.now()
        } returns current

        val taskInfoDto = TaskInfoDTO(
            "someTask",
            "14-06-2021",
            "Medium",
            "Weekly",
            "Just a test Task"
        )

        val parsedDueDate = formatter.parse(taskInfoDto.dueDate, Instant :: from)

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.insertTask(
                taskInfoDto.name,
                current,
                parsedDueDate,
                taskInfoDto.difficultyName,
                taskInfoDto.repetition,
                "username",
                taskInfoDto.description
            )
        } returns TaskDTO(
            1,
            taskInfoDto.name,
            current,
            parsedDueDate,
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
    fun `Insert a Test unsucessfully for inserting incorrect difficulty`(){
        val current = formatter.parse("20-05-2021", Instant :: from)
        coEvery {
            Instant.now()
        } returns current

        val taskInfoDto = TaskInfoDTO(
            "someTask",
            "14-06-2021",
            "I don't know",
            "Weekly",
            "Just a test Task"
        )

        val parsedDueDate = formatter.parse(taskInfoDto.dueDate, Instant :: from)

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.insertTask(
                taskInfoDto.name,
                current,
                parsedDueDate,
                taskInfoDto.difficultyName,
                taskInfoDto.repetition,
                "username",
                taskInfoDto.description
            )
        } throws InvalidDifficultyException("I don't know")

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Insert a Test unsucessfully for inserting incorrect repetition`(){
        val current = formatter.parse("20-05-2021", Instant :: from)
        coEvery {
            Instant.now()
        } returns current

        val taskInfoDto = TaskInfoDTO(
            "someTask",
            "14-06-2021",
            "Medium",
            "Sometimes",
            "Just a test Task"
        )

        val parsedDueDate = formatter.parse(taskInfoDto.dueDate, Instant :: from)

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.insertTask(
                taskInfoDto.name,
                current,
                parsedDueDate,
                taskInfoDto.difficultyName,
                taskInfoDto.repetition,
                "username",
                taskInfoDto.description
            )
        } throws InvalidRepetitionException("sometimes")

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }


}