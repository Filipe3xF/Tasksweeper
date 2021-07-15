package com.tasksweeper.utils

import com.tasksweeper.it.TasksweeperIT
import com.tasksweeper.mainModule
import io.ktor.application.*
import io.ktor.config.*

fun Application.integrationTestModule() {
    (environment.config as MapApplicationConfig).apply {
        put(
            "db.jdbcUrl",
            "${TasksweeperIT.postgresContainer.jdbcUrl}&user=${TasksweeperIT.postgresContainer.username}&password=${TasksweeperIT.postgresContainer.password}"
        )
    }
    mainModule()
}

fun Application.unitTestModule() {
    mainModule(true)
}