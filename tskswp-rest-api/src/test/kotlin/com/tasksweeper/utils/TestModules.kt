package com.tasksweeper.utils

import com.tasksweeper.it.TasksweeperIT
import com.tasksweeper.module
import io.ktor.application.*
import io.ktor.config.*

fun Application.integrationTestModule() {
    (environment.config as MapApplicationConfig).apply {
        put("db.jdbcUrl", TasksweeperIT.postgresContainer.jdbcUrl)
        put("db.dbUser", TasksweeperIT.postgresContainer.username)
        put("db.dbPassword", TasksweeperIT.postgresContainer.password)
    }

    module()
}

fun Application.unitTestModule() {
    module(true)
}