package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.AppError
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.module
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.service.AccountService
import com.tasksweeper.service.AccountStatusService
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
import java.time.Instant

class TaskControllerTest : KoinTest {
    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { TaskService() }
                single { AccountService() }
                single { AccountStatusService() }
                single { mockk<TaskRepository>() }
                single { mockk<AccountStatusRepository>() }
                single { mockk<AccountRepository>() }
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
            Instant.now(),
            Instant.now(),
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
            }.let {
                it.response.status() shouldBe HttpStatusCode.Created
                it.response.content shouldContain taskInfoDto.name
                it.response.content shouldContain taskInfoDto.description!!
                it.response.content shouldContain taskInfoDto.difficultyName
                it.response.content shouldContain taskInfoDto.repetition!!
                it.response.content shouldContain "username"
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
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    it.response.content,
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
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    it.response.content,
                    AppError::class.java
                ).error shouldContain taskInfoDto.repetition!!
            }
        }
    }

    @Test
    fun `Insert a Task unsuccessfully with a past due date`() {
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
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
                get<ObjectMapper>().readValue(
                    it.response.content,
                    AppError::class.java
                ).error shouldContain instantOf(taskInfoDto.dueDate!!, taskInfoDto.dueTime!!).toString()
            }
        }
    }

    @Test
    fun `Insert a Task unsuccessfully with an invalid due date`() {
        val date = DateDTO("2021", "13", "14")
        val time = TimeDTO("23", "59", "59")

        val taskInfoDto = TaskInfoDTO(
            "someTask",
            date,
            time,
            "Medium",
            "Weekly",
            "Just a test Task"
        )

        val timestamp = "${date.year}-${date.month}-${date.day}T${time.hour}:${time.minute}:${time.second}.000Z"

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskInfoDto))
            }.let {
                get<ObjectMapper>().readValue(
                    it.response.content,
                    AppError::class.java
                ).error shouldContain timestamp
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
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Deletes an account successfully and delivers the appropriate rewards`() {
        val taskDTO = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            "Daily",
            "username",
            "I'm describing a test Task"
        )

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO("username", "some@mail.com", "somepass", 1)

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatus("username")
        } returns listOf(
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.HP.dbName, AccountStatusValue.HP.initialValue),
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.EXP.dbName, AccountStatusValue.EXP.initialValue),
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.GOLD.dbName, AccountStatusValue.GOLD.initialValue)
        )
        coEvery {
            accountStatusRepository.updateStatus("username", any(), any())
        } returns 0

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } returns taskDTO
        coEvery {
            taskRepository.deleteTask(1)
        } returns 1

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskDTO))
            }.let {
                it.response.status() shouldBe HttpStatusCode.OK
                it.response.content shouldContain taskDTO.id.toString()
                it.response.content shouldContain taskDTO.accountName
                it.response.content shouldContain taskDTO.description!!
                it.response.content shouldContain taskDTO.difficultyName
                it.response.content shouldContain taskDTO.name
                it.response.content shouldContain taskDTO.repetitionName!!
            }
        }
    }

    @Test
    fun `Fails to deliver rewards because the user isn't logged in `() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/success") {
                addContentTypeHeader()
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Tried to get rewards for a task that doesn't exist`() {
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
            }
        }
    }

    @Test
    fun `Get an exception after trying to reap the rewards from another account`() {
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } returns TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "NotMyAccount",
            null
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "MyAccount")
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }

    }

    @Test
    fun `Deletes an account successfully and punishes the account accordingly`() {
        val taskDTO = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            "Daily",
            "username",
            "I'm describing a test Task"
        )

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO("username", "some@mail.com", "somepass", 1)

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatus("username")
        } returns listOf(
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.HP.dbName, AccountStatusValue.HP.initialValue),
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.EXP.dbName, AccountStatusValue.EXP.initialValue),
            AccountStatusDTO(taskDTO.accountName, AccountStatusValue.GOLD.dbName, AccountStatusValue.GOLD.initialValue)
        )
        coEvery {
            accountStatusRepository.updateStatus("username", any(), any())
        } returns 0

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } returns taskDTO
        coEvery {
            taskRepository.deleteTask(1)
        } returns 1

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(taskDTO))
            }.let {
                it.response.status() shouldBe HttpStatusCode.OK
                it.response.content shouldContain taskDTO.id.toString()
                it.response.content shouldContain taskDTO.accountName
                it.response.content shouldContain taskDTO.description!!
                it.response.content shouldContain taskDTO.difficultyName
                it.response.content shouldContain taskDTO.name
                it.response.content shouldContain taskDTO.repetitionName!!
            }
        }
    }

    @Test
    fun `Fails to punish account because the user isn't logged in `() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/failure") {
                addContentTypeHeader()
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Tried to fail a task that doesn't exist`() {
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
            }
        }
    }

    @Test
    fun `Get an exception after trying to punish an account with failed tasks from another user`() {
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } returns TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "NotMyAccount",
            null
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "MyAccount")
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }

    }
}
