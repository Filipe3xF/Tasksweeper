package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.*
import com.tasksweeper.repository.AccountConsumableRepository
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.repository.ConsumableStatusRepository
import com.tasksweeper.service.AccountConsumableService
import com.tasksweeper.service.AccountService
import com.tasksweeper.service.AccountStatusService
import com.tasksweeper.service.ConsumableStatusService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.unitTestModule
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
import kotlin.test.assertEquals

class AccountConsumableControllerTest : KoinTest {

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountStatusService() }
                single { AccountConsumableService() }
                single { AccountService() }
                single { ConsumableStatusService() }
                single { mockk<AccountStatusRepository>() }
                single { mockk<AccountConsumableRepository>() }
                single { mockk<AccountRepository>() }
                single { mockk<ConsumableStatusRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `Use Consumable Successfully`() {
        val accountRepository = get<AccountRepository>()

        val accountConsumableRepository = get<AccountConsumableRepository>()

        val accountStatusRepository = get<AccountStatusRepository>()

        val consumableStatusRepository = get<ConsumableStatusRepository>()

        coEvery {
            accountConsumableRepository.selectAccountConsumable(username = "username", consumableId = 1)
        } returns AccountConsumableDTO("username", 1, 1)

        coEvery {
            accountConsumableRepository.deleteAccountConsumable("username", 1)
        } returns 1

        coEvery {
            consumableStatusRepository.selectConsumableStatus(1)
        } returns ConsumableStatusDTO(
            consumableId = 1,
            statusName = "Health",
            value = 15,
            percentage = true,
            instant = false
        )

        coEvery {
            accountRepository.selectAccount("username")
        } returns AccountDTO("username", "username@username.com", "***", 1)

        coEvery {
            accountStatusRepository.selectAccountStatusByName("username", "Health")
        } returns AccountStatusDTO("username", "Health", 98)

        coEvery { accountStatusRepository.updateStatus("username", "Health", any()) } returns 1

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/accountConsumable/1/use") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                get<ObjectMapper>().readValue(response.content, AccountConsumableDTO::class.java)
                    .let { response ->
                        assertEquals("username", response.username)
                        assertEquals(1, response.consumableId)
                        assertEquals(0, response.quantity)
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
    fun `Use Consumable Without Account possessing any of said Consumable`() {
        val accountConsumableRepository = get<AccountConsumableRepository>()

        coEvery {
            accountConsumableRepository.selectAccountConsumable(username = "username", consumableId = 1)
        } returns null

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/accountConsumable/1/use") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
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
    fun `Try to use a consumable that doesn't exist`() {

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/accountConsumable/something/use") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
            }
        }
    }

    @Tests
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
