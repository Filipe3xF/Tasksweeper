package com.tasksweeper.it

import com.fasterxml.jackson.databind.ObjectMapper
import com.tasksweeper.controller.AccountStatusResponseDTO
import com.tasksweeper.controller.JwtDTO
import com.tasksweeper.controller.RegisterDTO
import com.tasksweeper.controller.TaskInfoDTO
import com.tasksweeper.entities.AccountStatusValue
import com.tasksweeper.entities.DifficultyMultiplier
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.entities.TaskStateValue
import com.tasksweeper.utils.addContentTypeHeader
import com.tasksweeper.utils.addJwtHeader
import com.tasksweeper.utils.integrationTestModule
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.koin.test.get

class TasksIT : TasksweeperIT() {

    @Test
    fun `Completing a task successfully provides experience and gold`() {
        withTestApplication(Application::integrationTestModule) {
            val objectMapper = get<ObjectMapper>()
            val user = RegisterDTO("username", "username@email.com", "password")

            var request = handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(objectMapper.writeValueAsString(user))
            }

            val jwt = objectMapper.readValue(request.response.content, JwtDTO::class.java).jwt

            val taskInfoDTO = TaskInfoDTO(
                "task",
                null,
                null,
                DifficultyMultiplier.HARD.dbName,
                null,
                null
            )

            request = handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(jwt)
                setBody(objectMapper.writeValueAsString(taskInfoDTO))
            }

            val task = objectMapper.readValue(request.response.content, TaskDTO::class.java)

            request = handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val initialAccountStatus =
                objectMapper.readValue(request.response.content, Array<AccountStatusResponseDTO>::class.java)

            request = handleRequest(HttpMethod.Patch, "/task/${task.id}/success") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val completedTask = objectMapper.readValue(request.response.content, TaskDTO::class.java)

            request = handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val finalAccountStatus =
                objectMapper.readValue(request.response.content, Array<AccountStatusResponseDTO>::class.java)

            completedTask.state shouldBe TaskStateValue.DONE.dbName

            finalAccountStatus.find { it.statusName == AccountStatusValue.HP.dbName }!!.value shouldBeExactly
                    initialAccountStatus.find { it.statusName == AccountStatusValue.HP.dbName }!!.value

            finalAccountStatus.find { it.statusName == AccountStatusValue.GOLD.dbName }!!.value shouldBeGreaterThan
                    initialAccountStatus.find { it.statusName == AccountStatusValue.GOLD.dbName }!!.value

            finalAccountStatus.find { it.statusName == AccountStatusValue.EXP.dbName }!!.value shouldBeGreaterThan
                    initialAccountStatus.find { it.statusName == AccountStatusValue.EXP.dbName }!!.value
        }
    }

    @Test
    fun `Completing a task unsuccessfully reduces hp`() {
        withTestApplication(Application::integrationTestModule) {
            val objectMapper = get<ObjectMapper>()
            val user = RegisterDTO("username", "username@email.com", "password")

            var request = handleRequest(HttpMethod.Post, "/register") {
                addContentTypeHeader()
                setBody(objectMapper.writeValueAsString(user))
            }

            val jwt = objectMapper.readValue(request.response.content, JwtDTO::class.java).jwt

            val taskInfoDTO = TaskInfoDTO(
                "task",
                null,
                null,
                DifficultyMultiplier.HARD.dbName,
                null,
                null
            )

            request = handleRequest(HttpMethod.Post, "/task") {
                addContentTypeHeader()
                addJwtHeader(jwt)
                setBody(objectMapper.writeValueAsString(taskInfoDTO))
            }

            val task = objectMapper.readValue(request.response.content, TaskDTO::class.java)

            request = handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val initialAccountStatus =
                objectMapper.readValue(request.response.content, Array<AccountStatusResponseDTO>::class.java)

            request = handleRequest(HttpMethod.Patch, "/task/${task.id}/failure") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val completedTask = objectMapper.readValue(request.response.content, TaskDTO::class.java)

            request = handleRequest(HttpMethod.Get, "/accountStatus") {
                addContentTypeHeader()
                addJwtHeader(jwt)
            }

            val finalAccountStatus =
                objectMapper.readValue(request.response.content, Array<AccountStatusResponseDTO>::class.java)

            completedTask.state shouldBe TaskStateValue.FAILED.dbName

            finalAccountStatus.find { it.statusName == AccountStatusValue.HP.dbName }!!.value shouldBeLessThan
                    initialAccountStatus.find { it.statusName == AccountStatusValue.HP.dbName }!!.value

            finalAccountStatus.find { it.statusName == AccountStatusValue.GOLD.dbName }!!.value shouldBeExactly
                    initialAccountStatus.find { it.statusName == AccountStatusValue.GOLD.dbName }!!.value

            finalAccountStatus.find { it.statusName == AccountStatusValue.EXP.dbName }!!.value shouldBeExactly
                    initialAccountStatus.find { it.statusName == AccountStatusValue.EXP.dbName }!!.value
        }
    }
}