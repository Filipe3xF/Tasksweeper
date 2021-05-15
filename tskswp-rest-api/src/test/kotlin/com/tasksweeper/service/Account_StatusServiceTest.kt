package com.tasksweeper.service

import org.koin.test.get
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.Account_StatusDTO
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.Account_StatusRepository
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
import org.koin.test.get
import org.koin.test.KoinTest
import kotlin.test.assertEquals

class Account_StatusServiceTest : KoinTest{


    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { Account_StatusService() }
                single { mockk<Account_StatusRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun ` Add the status and its corresponding values to a pre-existing account`(){
        val username = "Tangerina"

        val accountStatusrepository = get<Account_StatusRepository>()
        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Health",
                5
            )
        } returns Account_StatusDTO(username, "Health", 5)


        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Gold",
                0
            )
        } returns Account_StatusDTO(username, "Gold", 0)

        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Experience",
                0
            )
        } returns Account_StatusDTO(username, "Exp", 0)

        val account_StatusService = Account_StatusService()

        runBlocking {
            val list = account_StatusService.insertInitialStatus(username)

            val health = list.filter { it?.status_name == "Health" }.single()
            assertEquals(username, health?.username)
            assertEquals("Health", health?.status_name)
            assertEquals(5, health?.value)

            val gold = list.filter { it?.status_name == "Gold" }.single()
            assertEquals(username, gold?.username)
            assertEquals("Gold", gold?.status_name)
            assertEquals(0, gold?.value)

            val exp = list.filter { it?.status_name == "Exp" }.single()
            assertEquals(username, gold?.username)
            assertEquals("Exp", exp?.status_name)
            assertEquals(0, exp?.value)
        }
    }

    @Test
    fun ` Try to add status to an account that doesn't exist`(){
        val username = "Tangerina"

        val accountStatusrepository = get<Account_StatusRepository>()
        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Health",
                5
            )
        }.throws(NotFoundException())


        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Gold",
                0
            )
        } .throws(NotFoundException())

        coEvery {
            accountStatusrepository.insertAccount_Status(
                username,
                "Experience",
                0
            )
        } .throws(NotFoundException())

        val account_StatusService = Account_StatusService()

        runBlocking {
            assertThrows<NotFoundException> {
                val list = account_StatusService.insertInitialStatus(username)
            }
        }
    }

}