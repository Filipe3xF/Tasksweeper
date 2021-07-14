package com.tasksweeper.it

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.testcontainers.containers.PostgreSQLContainer

open class TasksweeperIT : KoinTest {

    @BeforeEach
    fun start() {
        postgresContainer.start()
    }

    @AfterEach
    fun stop() {
        postgresContainer.stop()
        stopKoin()
    }

    companion object {
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres")
    }
}
