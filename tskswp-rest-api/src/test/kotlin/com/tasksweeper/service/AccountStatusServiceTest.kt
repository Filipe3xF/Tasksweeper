package com.tasksweeper.service

import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.repository.AccountStatusRepository
import io.kotest.matchers.shouldBe
import io.ktor.features.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class AccountStatusServiceTest : KoinTest{

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountStatusService() }
                single { mockk<AccountStatusRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `Add the status and its corresponding values to a pre-existing account`(){
        val username = "Tangerina"

        val accountStatusrepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Health",
                5
            )
        } returns AccountStatusDTO(username, "Health", 5)


        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Gold",
                0
            )
        } returns AccountStatusDTO(username, "Gold", 0)

        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Experience",
                0
            )
        } returns AccountStatusDTO(username, "Experience", 0)

        val accountStatusService = AccountStatusService()

        runBlocking {
            val list = accountStatusService.insertInitialStatus(username)

            list.single { it?.statusName == "Health" }!!.let{
                it.username shouldBe username
                it.statusName shouldBe "Health"
                it.value shouldBe 5
            }
            list.single { it?.statusName == "Gold" }!!.let {
                it.username shouldBe username
                it.statusName shouldBe "Gold"
                it.value shouldBe 0
            }
            list.single { it?.statusName == "Experience" }!!.let {
                it.username shouldBe username
                it.statusName shouldBe "Experience"
                it.value shouldBe 0
            }
        }
    }

    @Test
    fun `Try to add status to an account that doesn't exist`(){
        val username = "Tangerina"

        val accountStatusrepository = get<AccountStatusRepository>()
        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Health",
                5
            )
        } throws NotFoundException()

        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Gold",
                0
            )
        }  throws NotFoundException()

        coEvery {
            accountStatusrepository.insertAccountStatus(
                username,
                "Experience",
                0
            )
        } throws NotFoundException()

        val accountStatusService = AccountStatusService()

        runBlocking {
            assertThrows<NotFoundException> {
                accountStatusService.insertInitialStatus(username)
            }
        }
    }
}