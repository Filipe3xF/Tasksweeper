package com.tasksweeper.repository

import com.tasksweeper.entities.AccountStatus
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class AccountStatusRepository {

    suspend fun deleteAccountStatus(accountName: String) = transaction {
        AccountStatus.deleteWhere {
            AccountStatus.username eq accountName
        }
    }

    suspend fun insertAccountStatus(accountName: String, statusName: String, parameter: Long) = transaction {
        AccountStatus.insert {
            it[username] = accountName
            it[this.statusName] = statusName
            it[value] = parameter
        }.resultedValues?.first()?.let { toAccountStatus(it) }
    }

    suspend fun selectAccountStatus(username: String) = transaction {
        AccountStatus.select {
            AccountStatus.username eq username
        }.map { toAccountStatus(it) }
    }

    suspend fun updateStatus(username : String, statusName: String, newValue : Long) = transaction{
        AccountStatus.update({ (AccountStatus.username eq username) and (AccountStatus.statusName eq statusName) }) {
            it[value] = newValue
        }
    }

    private fun toAccountStatus(row: ResultRow): AccountStatusDTO = AccountStatusDTO(
        username = row[AccountStatus.username],
        statusName = row[AccountStatus.statusName],
        value = row[AccountStatus.value]
    )
}
