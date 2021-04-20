package com.tasksweeper

import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import mu.KotlinLogging
import org.apache.commons.cli.*
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

private enum class Arguments(val argumentKey: String, val description: String) {
    URL("url", "The database url."),
    USERNAME("username", "User to connect to the database."),
    PASSWORD("password", "The user's password.")
}

private const val CHANGELOG_FILE = "changelog.xml"

fun main(args: Array<String>) {
    logger.info { "Database Updater Started."}

    parseArguments(args).let {
        Properties().apply {
            setProperty(Arguments.URL.argumentKey, it.getOptionValue(Arguments.URL.argumentKey))
            setProperty(Arguments.USERNAME.argumentKey, it.getOptionValue(Arguments.USERNAME.argumentKey))
            setProperty(Arguments.PASSWORD.argumentKey, it.getOptionValue(Arguments.PASSWORD.argumentKey))
        }
    }.let {
        DatabaseUpdater(it).update()
    }
}

private fun parseArguments(args: Array<String>): CommandLine {
    val expectedParameters = Options()

    Arguments.values().map {
        Option.builder(it.argumentKey).argName(it.argumentKey).desc(it.description).hasArg().required().build()
    }.forEach {
        expectedParameters.addOption(it)
    }

    try {
        return DefaultParser().parse(expectedParameters, args)
    } catch (e: ParseException) {
        HelpFormatter().printHelp("database-updater", expectedParameters)
        exitProcess(1)
    }
}

private class DatabaseUpdater(val properties: Properties) {

    fun update() {
        var connection: Connection? = null
        try {
            connection = DriverManager.getConnection(
                properties.getProperty(Arguments.URL.argumentKey),
                properties.getProperty(Arguments.USERNAME.argumentKey),
                properties.getProperty(Arguments.PASSWORD.argumentKey)
            )

            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection)).let {
                Liquibase(CHANGELOG_FILE, ClassLoaderResourceAccessor(javaClass.classLoader), it).update("")
                logger.info { "Database updated successfully!" }
            }

            connection.close()
        } catch (exception: Exception) {
            connection?.close()
            logger.error(exception) { "Database update failed due to the following error:" }
        }
    }
}