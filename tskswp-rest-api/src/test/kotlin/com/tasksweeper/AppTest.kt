package com.tasksweeper

import com.google.gson.Gson
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class AppTest {
    @Test
    fun `hello world endpoint is working`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                response.status() shouldBe HttpStatusCode.OK
                Gson().fromJson(response.content, HelloWorldMessage::class.java).message shouldBe "Hello, World!"
            }
        }
    }

    data class HelloWorldMessage(
        val message: String
    )
}
