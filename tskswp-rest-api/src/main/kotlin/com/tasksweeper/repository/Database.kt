package com.tasksweeper.repository

import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.*
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource

object DatabaseFactory {
    private const val CHANGELOG_FILE = "changelog.xml"
    private val logger = KotlinLogging.logger { }

    fun init(appConfig: ApplicationConfig) {
        hikari(appConfig).let {
            Database.connect(it)
            runLiquibase(it)
        }
    }

    private fun hikari(appConfig: ApplicationConfig): HikariDataSource = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = appConfig.property("db.jdbcUrl").getString()
        maximumPoolSize = 3
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }.let {
        HikariDataSource(it)
    }

    private fun runLiquibase(dataSource: DataSource) {
        DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection)).let {
            Liquibase(CHANGELOG_FILE, ClassLoaderResourceAccessor(javaClass.classLoader), it).update("")
        }
    }

    suspend fun <T> transaction(block: suspend () -> T): T = newSuspendedTransaction {
        try {
            block()
        } catch (exception: Exception) {
            logger.error(exception) { "Transaction failed due to the following exception:" }
            when (exception) {
                is NoSuchElementException -> throw DatabaseNotFoundException()
                else -> throw exception
            }
        }
    }
}
