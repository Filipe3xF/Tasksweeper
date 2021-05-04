package com.tasksweeper

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.authentication.JWT
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get

class AppTest : KoinTest {

    @BeforeEach
    fun start() {
        startKoin {
            modules(module(createdAtStart = true) {
                single { JWT() }
            })
        }
    }

    @AfterEach
    fun stop() {
        stopKoin()
    }

    @Test
    fun `hello world endpoint is working`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK
                get<ObjectMapper>().readValue(
                    response.content,
                    HelloWorldMessage::class.java
                ).message shouldBe "Hello, World!"
            }
        }
    }

    data class HelloWorldMessage(
        val message: String
    )
}
