package com.tasksweeper.repository

import com.tasksweeper.entities.Account
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

suspend fun insertAccount(accountUsername: String, accountEmail: String, accountPassword: String) = transaction {
    Account.insert {
        it[username] = accountUsername
        it[email] = accountEmail
        it[password] = accountPassword
    }
}

suspend fun selectAccount(accountUsername: String) = transaction {
    Account.select {
        Account.username eq accountUsername
    }.single()
}

