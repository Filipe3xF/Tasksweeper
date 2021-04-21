package com.tasksweeper

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.ktor.ext.get
import kotlin.test.Test

class AppTest {
    @Test
    fun `hello world endpoint is working`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK

                val objectMapper: ObjectMapper = get()
                objectMapper.readValue(response.content, HelloWorldMessage::class.java)
            }
        }
    }

    data class HelloWorldMessage(
        val message: String
    )
}
