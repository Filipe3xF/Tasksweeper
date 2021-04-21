package com.tasksweeper.service

import com.tasksweeper.entities.Account
import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.exceptions.InvalidCredentialsException
import com.tasksweeper.exceptions.RegisterException
import com.tasksweeper.repository.insertAccount
import com.tasksweeper.repository.selectAccount
import org.jetbrains.exposed.sql.ResultRow
import org.mindrot.jbcrypt.BCrypt

suspend fun registerAccount(accountUsername: String, accountEmail: String, accountPassword: String): AccountDTO =
    insertAccount(
        accountUsername,
        accountEmail,
        BCrypt.hashpw(accountPassword, BCrypt.gensalt())
    ).resultedValues?.first()?.let {
        toAccount(it)
    } ?: throw RegisterException(accountUsername)

suspend fun checkAccount(accountUsername: String, accountPassword: String): AccountDTO =
    toAccount(selectAccount(accountUsername)).also {
        if (!BCrypt.checkpw(accountPassword, it.password))
            throw InvalidCredentialsException()
    }

private fun toAccount(row: ResultRow): AccountDTO = AccountDTO(
    username = row[Account.username],
    email = row[Account.email],
    password = row[Account.password]
)