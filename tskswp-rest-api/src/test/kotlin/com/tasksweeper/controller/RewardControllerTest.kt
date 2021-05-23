package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.AppError
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.module
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.service.AccountService
import com.tasksweeper.service.AccountStatusService
import com.tasksweeper.service.RewardService
import com.tasksweeper.service.TaskService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
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

class RewardControllerTest : KoinTest {
    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountService() }
                single { AccountStatusService() }
                single { TaskService() }
                single { RewardService() }
                single { mockk<AccountRepository>() }
                single { mockk<AccountStatusRepository>() }
                single { mockk<TaskRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `Deletes an account successfully and delivers the appropriate rewards`() {
        val reward = RewardDTO(1)

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO("username", "some@mail.com","somepass",1)

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatus("username")
        } returns listOf(
            AccountStatusDTO("username", "Health", 5),
            AccountStatusDTO("username","Experience" , 0),
            AccountStatusDTO("username","Gold" , 0)
        )
        coEvery {
            accountStatusRepository.updateStatus("username", "Experience", any())
        } returns 0
        coEvery {
            accountStatusRepository.updateStatus("username", "Gold", any())
        } returns 0

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        }returns TaskDTO(
            1,
            "sometask",
            Instant.now(),
            Instant.now(),
            "Easy",
            null,
            "username",
            null
        )
        coEvery {
            taskRepository.deleteTask(1)
        } returns 1

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/reward") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(reward))
            }.let {
                it.response.status() shouldBe HttpStatusCode.Accepted
                it.response.content shouldContain "Task with id: 1 was deleted successfully"
            }
        }
    }

    @Test
    fun `Fails to deliver rewards because the username is not valid`() {
        val reward = RewardDTO(1)

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/reward") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(reward))
            }.let {
                it.response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }

    @Test
    fun `Tried to get rewards for a task that doesn't exist`() {
        val reward = RewardDTO(1)

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO("username", "some@mail.com","somepass",1)

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatus("username")
        } returns listOf(
            AccountStatusDTO("username", "Health", 5),
            AccountStatusDTO("username","Experience" , 0),
            AccountStatusDTO("username","Gold" , 0)
        )
        coEvery {
            accountStatusRepository.updateStatus("username", "Experience", any())
        } returns 0
        coEvery {
            accountStatusRepository.updateStatus("username", "Gold", any())
        } returns 0

        val taskRepository = get<TaskRepository>()
        coEvery {
            taskRepository.selectTask(1)
        } throws DatabaseNotFoundException()
        coEvery {
            taskRepository.deleteTask(1)
        } returns 1

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/reward") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
                setBody(get<ObjectMapper>().writeValueAsString(reward))
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldBe "The desired element was not found in the database."
            }
        }
    }

}