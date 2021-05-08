package com.tasksweeper.controller

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.exceptions.AppError
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.module
import com.tasksweeper.repository.AccountRepository
import com.tasksweeper.service.AccountService
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mindrot.jbcrypt.BCrypt
import org.postgresql.util.PSQLException

const val ACCOUNT_LEVEL : Int = 1


class AccountControllerTest : KoinTest {


    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
                single { AccountService() }
                single { mockk<AccountRepository>() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `registers an account successfully`() {
        val register = RegisterDTO(
            "TaskSweeperUser",
            "user@mail.com",
            "password"
        )

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.insertAccount(
                register.username,
                register.email,
                any()
            )
        } returns AccountDTO(register.username, register.email, register.password, ACCOUNT_LEVEL)


        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(register))
            }.apply {
                response.status() shouldBe HttpStatusCode.Created

                shouldNotThrow<JWTVerificationException> {
                    get<JWT>().verifier.verify(
                        get<ObjectMapper>().readValue(
                            response.content,
                            JwtDTO::class.java
                        ).jwt
                    )
                }
            }
        }
    }

    @Test
    fun `logins an account successfully`() {
        val login = LoginDTO(
            "TaskSweeperUser",
            "password"
        )

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(login.username) } returns AccountDTO(
            login.username,
            "user@mail.com",
            BCrypt.hashpw(login.password, BCrypt.gensalt()),
            1
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/login") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(login))
            }.apply {
                response.status() shouldBe HttpStatusCode.OK

                shouldNotThrow<JWTVerificationException> {
                    get<JWT>().verifier.verify(
                        get<ObjectMapper>().readValue(
                            response.content,
                            JwtDTO::class.java
                        ).jwt
                    )
                }
            }
        }
    }

    @Test
    fun `register handles database constraint error`() {
        val register = RegisterDTO(
            "TaskSweeperUser",
            "user@mail.com",
            "password"
        )

        val errorMessage = "Database constraint error."

        val accountRepository = get<AccountRepository>()
        coEvery {
            accountRepository.insertAccount(
                register.username,
                register.email,
                any()
            )
        } throws mockk<ExposedSQLException> {
            every { message } returns errorMessage
            every { cause } returns mockk<PSQLException>()
        }

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(register))
            }.apply {
                response.status() shouldBe HttpStatusCode.Conflict

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldBe errorMessage
            }
        }
    }

    @Test
    fun `login handles database constraint error`() {
        val login = LoginDTO(
            "TaskSweeperUser",
            "password"
        )

        val errorMessage = "Database constraint error."

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(login.username) } throws mockk<ExposedSQLException> {
            every { message } returns errorMessage
            every { cause } returns mockk<PSQLException>()
        }

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/login") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(login))
            }.apply {
                response.status() shouldBe HttpStatusCode.Conflict

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldBe errorMessage
            }
        }
    }

    @Test
    fun `login handles non existent user`() {
        val login = LoginDTO(
            "TaskSweeperUser",
            "password"
        )

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(login.username) } throws DatabaseNotFoundException()

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/login") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(login))
            }.apply {
                response.status() shouldBe HttpStatusCode.NotFound

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain "not found"
            }
        }
    }

    @Test
    fun `logins handles invalid password`() {
        val login = LoginDTO(
            "TaskSweeperUser",
            "password"
        )

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(login.username) } returns AccountDTO(
            login.username,
            "user@mail.com",
            BCrypt.hashpw(login.password.plus("wildcard"), BCrypt.gensalt()),
            ACCOUNT_LEVEL
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/login") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(login))
            }.apply {
                response.status() shouldBe HttpStatusCode.Unauthorized

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain "invalid credentials"
            }
        }
    }

    @Test
    fun `gets an account info successfully`() {
        val account = AccountDTO(
            "username",
            "username@email.com",
            "password",
            ACCOUNT_LEVEL
        )

        val accountRepository = get<AccountRepository>()
        coEvery { accountRepository.selectAccount(account.username) } returns AccountDTO(
            account.username,
            account.email,
            BCrypt.hashpw(account.password, BCrypt.gensalt()),
            ACCOUNT_LEVEL
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/account") {
                addJwtHeader(get(), account.username)
            }.apply {
                response.status() shouldBe HttpStatusCode.OK
                response.content shouldContain account.username
                response.content shouldContain account.email
                response.content shouldNotContain account.password
            }
        }
    }

    @Test
    fun `register handles invalid username`() {
        val register = RegisterDTO(
            "Invalid username .-$",
            "user@mail.com",
            "password"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(register))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain "username '${register.username}' is not valid"
            }
        }
    }

    @Test
    fun `register handles invalid email`() {
        val register = RegisterDTO(
            "TaskSweeperUser",
            "user.dfs dsfsd @mai dsfl.corgm",
            "password"
        )

        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(get<ObjectMapper>().writeValueAsString(register))
            }.apply {
                response.status() shouldBe HttpStatusCode.BadRequest

                get<ObjectMapper>().readValue(
                    response.content,
                    AppError::class.java
                ).error shouldContain "email '${register.email}' is not valid"
            }
        }
    }
}
