package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue.*
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.entities.TaskStateValue.*
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
            taskInfoDto.description,
            TO_DO.dbName
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
    fun `Closes a task successfully and delivers the appropriate rewards`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            "Daily",
            "username",
            "I'm describing a test Task",
            TO_DO.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task
        coEvery {
            taskRepository.updateTaskState(task.id, DONE)
        } returns 1

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount(task.accountName)
        } returns AccountDTO(task.accountName, "some@mail.com", "somepass", 1)

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatusByName(task.accountName, GOLD.dbName)
        } returns AccountStatusDTO(task.accountName, GOLD.dbName, 10)
        coEvery {
            accountStatusRepository.updateStatus(task.accountName, GOLD.dbName, any())
        } returns 20

        coEvery {
            accountStatusRepository.selectAccountStatusByName(task.accountName, EXP.dbName)
        } returns AccountStatusDTO(task.accountName, EXP.dbName, 0)
        coEvery {
            accountStatusRepository.updateStatus(task.accountName, EXP.dbName, any())
        } returns 20

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(task))
            }.let {
                it.response.status() shouldBe HttpStatusCode.OK
                it.response.content shouldContain task.id.toString()
                it.response.content shouldContain task.accountName
                it.response.content shouldContain task.description!!
                it.response.content shouldContain task.difficultyName
                it.response.content shouldContain task.name
                it.response.content shouldContain task.repetitionName!!
            }
        }
    }

    @Test
    fun `Closing a task successfully gives out an error when taskId is not a number`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/NotANumber/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Fails to deliver rewards because the user isn't logged in `() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/1/success") {
                addContentTypeHeader()
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Tried to get rewards for a task that doesn't exist`() {
        val taskId = 1L
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(taskId)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/$taskId/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
            }
        }
    }

    @Test
    fun `Get an exception after trying to reap the rewards from another account`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "NotMyAccount",
            null,
            TO_DO.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "MyAccount")
            }.apply {
                response.status() shouldBe HttpStatusCode.Forbidden
            }
        }
    }

    @Test
    fun `Get an exception after trying to successfully close a task that is already in a close state`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "username",
            null,
            FAILED.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/success") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Fails a task and punishes the account accordingly`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            "Daily",
            "username",
            "I'm describing a test Task",
            TO_DO.dbName
        )

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO(task.accountName, "some@mail.com", "somepass", 1)

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task
        coEvery {
            taskRepository.updateTaskState(task.id, FAILED)
        } returns 1

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatusByName(task.accountName, HP.dbName)
        } returns AccountStatusDTO(task.accountName, HP.dbName, HP.initialValue)
        coEvery {
            accountStatusRepository.updateStatus(task.accountName, HP.dbName, any())
        } returns 80

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(task))
            }.let {
                it.response.status() shouldBe HttpStatusCode.OK
                it.response.content shouldContain task.id.toString()
                it.response.content shouldContain task.accountName
                it.response.content shouldContain task.description!!
                it.response.content shouldContain task.difficultyName
                it.response.content shouldContain task.name
                it.response.content shouldContain task.repetitionName!!
            }
        }
    }

    @Test
    fun `Closing a task unsuccessfully gives out an error when taskId is not a number`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/NotANumber/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Fails to punish account because the user isn't logged in `() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/1/failure") {
                addContentTypeHeader()
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Tried to fail a task that doesn't exist`() {
        val taskId = 1L
        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(taskId)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/$taskId/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
            }
        }
    }

    @Test
    fun `Get an exception after trying to punish an account with failed tasks from another user`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "NotMyAccount",
            null,
            TO_DO.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "MyAccount")
            }.apply {
                response.status() shouldBe HttpStatusCode.Forbidden
            }
        }
    }

    @Test
    fun `Get an exception after trying to unsuccessfully close a task that is already in a close state`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "username",
            null,
            DONE.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Patch, "/task/${task.id}/failure") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Deletes a task successfully`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "username",
            null,
            DONE.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        coEvery {
            taskRepository.deleteTask(task.id)
        } returns 1

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/${task.id}") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldContain task.id.toString()
                response.content shouldContain task.accountName
                response.content shouldContain task.difficultyName
                response.content shouldContain task.name
            }
        }
    }

    @Test
    fun `Fails to delete a task from another user`() {
        val task = TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "NotMyAccount",
            null,
            TO_DO.dbName
        )

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(task.id)
        } returns task

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/${task.id}") {
                addContentTypeHeader()
                addJwtHeader(get(), "MyAccount")
            }.apply {
                response.status() shouldBe HttpStatusCode.Forbidden
            }
        }
    }

    @Test
    fun `Deleting a task gives out an error when taskId is not a number`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/NotANumber") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.let {
                it.response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Test
    fun `Fails to delete task because the user isn't logged in`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/1") {
                addContentTypeHeader()
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Trying to delete a task that does not exist gives out an error`() {
        val taskId = 1L

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(taskId)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Delete, "/task/$taskId") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
            }
        }
    }
}
