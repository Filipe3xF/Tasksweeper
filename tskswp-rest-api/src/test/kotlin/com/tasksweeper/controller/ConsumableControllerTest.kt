package com.tasksweeper.controller

import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountConsumableDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.ConsumableDTO
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.repository.AccountConsumableRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.repository.ConsumableRepository
import com.tasksweeper.service.AccountConsumableService
import com.tasksweeper.service.AccountStatusService
import com.tasksweeper.service.ConsumableService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.unitTestModule
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

class ConsumableControllerTest : KoinTest {

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { ConsumableService() }
                single { mockk<ConsumableRepository>() }
                single { AccountStatusService() }
                single { mockk<AccountStatusRepository>() }
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
    fun `The item is purchased and then associated with the account`() {
        val username = "John"

        val consumable = ConsumableDTO(
            id = 1,
            name = "Health Potion",
            price = 20,
            description = "Recovers 15% of max HP"
        )

        val consumableRepository = get<ConsumableRepository>()
        coEvery {
            consumableRepository.selectConsumable(1)
        } returns consumable

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatusByName(username, "Gold")
        } returns AccountStatusDTO(username, "Gold", 20)
        coEvery {
            accountStatusRepository.updateStatus(username, "Gold", 0)
        } returns 0

        val accountConsumableRepository = get<AccountConsumableRepository>()
        coEvery {
            accountConsumableRepository.increaseQuantity(username, consumable.id)
        } returns 0
        coEvery {
            accountConsumableRepository.insertAccountConsumable(username, consumable.id)
        } returns AccountConsumableDTO(username, consumable.id, 1)

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/consumable/1/buy") {
                addContentTypeHeader()
                addJwtHeader(get(), username)
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldContain consumable.id.toString()
                response.content shouldContain consumable.name
                response.content shouldContain consumable.price.toString()
                response.content shouldContain consumable.description
            }
        }
    }

    @Test
    fun `The user tries to buy an item without having the currency`() {
        val username = "John"

        val consumable = ConsumableDTO(
            id = 1,
            name = "Health Potion",
            price = 20,
            description = "Recovers 15% of max HP"
        )

        val consumableRepository = get<ConsumableRepository>()
        coEvery {
            consumableRepository.selectConsumable(consumable.id)
        } returns consumable

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusRepository.selectAccountStatusByName(username, "Gold")
        } returns AccountStatusDTO(username, "Gold", 0)

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/consumable/${consumable.id}/buy") {
                addContentTypeHeader()
                addJwtHeader(get(), username)
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest
                response.content shouldContain "The user $username doesn't have enough gold to purchase the item"
            }
        }
    }

    @Test
    fun `The user tries to buy a non-existing item`() {
        val username = "John"

        val consumable = ConsumableDTO(
            id = 1,
            name = "Health Potion",
            price = 20,
            description = "Recovers 15% of max HP"
        )

        val consumableRepository = get<ConsumableRepository>()
        coEvery {
            consumableRepository.selectConsumable(consumable.id)
        } throws DatabaseNotFoundException()

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Post, "/consumable/${consumable.id}/buy") {
                addContentTypeHeader()
                addJwtHeader(get(), username)
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound
                response.content shouldContain "The desired element was not found in the database."
            }
        }
    }

    @Test
    fun `Gets all consumables`() {
        val consumableList = listOf(
            ConsumableDTO(
                1,
                "Health Potion",
                20,
                "Restores 15% of your max HP"
            )
        )

        val consumableRepository = get<ConsumableRepository>()
        coEvery {
            consumableRepository.getAllConsumables()
        } returns consumableList

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "/consumables") {
                addContentTypeHeader()
                addJwtHeader(get(), "username")
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content.let {
                    consumableList.forEach { consumable ->
                        it shouldContain consumable.name
                    }
                }
            }
        }
    }

    @Test
    fun `Fails to get all consumables without a jwt`() {
        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "/consumables") {
                addContentTypeHeader()
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }
}
