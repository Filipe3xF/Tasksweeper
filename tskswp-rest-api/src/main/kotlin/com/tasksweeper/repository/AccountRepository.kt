package com.tasksweeper.repository

import com.tasksweeper.entities.Account
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.exceptions.RegisterException
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class AccountRepository {

    suspend fun insertAccount(accountUsername: String, accountEmail: String, accountPassword: String) = transaction {
        Account.insert {
            it[username] = accountUsername
            it[email] = accountEmail
            it[password] = accountPassword
        }
    }.resultedValues?.first()?.let { toAccount(it) } ?: throw RegisterException(accountUsername)

    suspend fun selectAccount(accountUsername: String) = transaction {
        Account.select {
            Account.username eq accountUsername
        }.single().let { toAccount(it) }
    }

    private fun toAccount(row: ResultRow): AccountDTO = AccountDTO(
        username = row[Account.username],
        email = row[Account.email],
        password = row[Account.password]
    )
}