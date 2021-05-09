package com.tasksweeper.repository

import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val logger = KotlinLogging.logger { }

    fun init() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = appConfig.property("db.jdbcUrl").getString()
        username = appConfig.property("db.dbUser").getString()
        password = appConfig.property("db.dbPassword").getString()
        maximumPoolSize = 3
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }.let {
        HikariDataSource(it)
    }

    suspend fun <T> transaction(block: () -> T): T = withContext(Dispatchers.IO) {
        org.jetbrains.exposed.sql.transactions.transaction {
            try {
                block()
            } catch (exception: Exception) {
                logger.error(exception) { "Transaction failed due to the following exception:" }
                when (exception) {
                    is NoSuchElementException -> throw  DatabaseNotFoundException()
                    else -> throw exception
                }
            }
        }
    }
}
