package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountConsumableDTO
import com.tasksweeper.repository.AccountConsumableRepository
import com.tasksweeper.service.AccountConsumableService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.unitTestModule
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
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

class AccountConsumableControllerTest : KoinTest {

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountConsumableService() }
                single { mockk<AccountConsumableRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `should retrieve the user's consumable list`() {
        val username = "username"
        val firstConsumable = AccountConsumableDTO(
            username,
            1,
            3
        )
        val secondConsumable = AccountConsumableDTO(
            username,
            2,
            1
        )

        val accountConsumableRepository = get<AccountConsumableRepository>()
        coEvery {
            accountConsumableRepository.selectAccountConsumables(username)
        } returns listOf(firstConsumable, secondConsumable)

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "account/consumables") {
                addContentTypeHeader()
                addJwtHeader(get(), username)
            }
        }.apply {
            response.status() shouldBe HttpStatusCode.OK
            get<ObjectMapper>().readValue(response.content, Array<AccountConsumableDTO>::class.java).let { list ->
                list.first { it.consumableId == firstConsumable.consumableId }.quantity shouldBe firstConsumable.quantity
                list.first { it.consumableId == secondConsumable.consumableId }.quantity shouldBe secondConsumable.quantity
            }
        }
    }

    @Test
    fun `should retrieve an empty list when user has no consumables`() {
        val username = "username"

        val accountConsumableRepository = get<AccountConsumableRepository>()
        coEvery {
            accountConsumableRepository.selectAccountConsumables(username)
        } returns emptyList()

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "account/consumables") {
                addContentTypeHeader()
                addJwtHeader(get(), username)
            }
        }.apply {
            response.status() shouldBe HttpStatusCode.OK
            get<ObjectMapper>().readValue(response.content, Array<AccountConsumableDTO>::class.java).let { list ->
                list.shouldBeEmpty()
            }
        }
    }

    @Test
    fun `should give an error when fetching a user's consumables without JWT`() {
        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "account/consumables") {
                addContentTypeHeader()
            }
        }.apply {
            response.status() shouldBe HttpStatusCode.Unauthorized
        }
    }
}