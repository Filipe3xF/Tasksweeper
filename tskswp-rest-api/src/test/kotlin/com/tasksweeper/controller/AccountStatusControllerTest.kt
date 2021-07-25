package com.tasksweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue.*
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.repository.AccountStatusRepository
import com.tasksweeper.service.AccountService
import com.tasksweeper.service.AccountStatusService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.unitTestModule
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

class AccountStatusControllerTest : KoinTest {

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountStatusService() }
                single { AccountService() }
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
    fun `Gets all account status successfully`() {
        val account = AccountDTO(
            "username",
            "username@email.com",
            "password",
            1
        )

        val accountStatusList = listOf(
            AccountStatusDTO(
                account.username,
                HP.dbName,
                20
            ),
            AccountStatusDTO(
                account.username,
                EXP.dbName,
                30
            ),
            AccountStatusDTO(
                account.username,
                GOLD.dbName,
                30
            )
        )

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(account.username) } returns account

        val accountStatusRepository = get<AccountStatusRepository>()
        coEvery { accountStatusRepository.selectAccountStatus(account.username) } returns accountStatusList

        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
                addJwtHeader(get(), account.username)
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                get<ObjectMapper>().readValue(response.content, Array<AccountStatusResponseDTO>::class.java)
                    .let { response ->
                        response.find { it.statusName == HP.dbName }?.maxValue shouldBe 100
                        response.find { it.statusName == EXP.dbName }?.maxValue shouldBe 50
                        response.find { it.statusName == GOLD.dbName }?.maxValue shouldBe 999999999
                    }
            }
        }
    }

    @Test
    fun `Fails to get account status without a jwt`() {
        withTestApplication(Application::unitTestModule) {
            handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized
            }
        }
    }
}
