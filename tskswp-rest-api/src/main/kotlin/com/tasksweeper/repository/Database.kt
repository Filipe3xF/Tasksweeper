package com.tasksweeper.repository

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())

    fun init() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = appConfig.property("db.jdbcUrl").getString()
        username = appConfig.property("db.dbUser").getString()
        password = appConfig.property("db.dbPassword").getString()
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }.let {
        HikariDataSource(it)
    }
}

suspend fun <T> transaction(block: () -> T): Unit = withContext(Dispatchers.IO) {
    transaction { block() }
}
