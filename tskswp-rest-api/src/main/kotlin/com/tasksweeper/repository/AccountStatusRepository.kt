package com.tasksweeper.repository

import com.tasksweeper.entities.*
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class AccountStatusRepository {

    suspend fun deleteAccountStatus(accountName: String) = transaction {
        AccountStatus.deleteWhere {
            AccountStatus.username eq accountName
        }
    }

    suspend fun insertAccountStatus(accountName: String, statusName: String, parameter: Int) = transaction {
        AccountStatus.insert {
            it[username] = accountName
            it[this.statusName] = statusName
            it[value] = parameter
        }.resultedValues?.first()?.let { toAccountStatus(it) }
    }

    suspend fun selectAccountStatus(username: String) = transaction {
        AccountStatus.select {
            AccountStatus.username eq username
        }.let { toAccountStatusList(it) }
    }

    private fun toAccountStatusList(query: Query): List<AccountStatusDTO> {
        val list = mutableListOf<AccountStatusDTO>()
        query.forEach { list.add(toAccountStatus(it)) }
        return list
    }

    private fun toAccountStatus(row: ResultRow): AccountStatusDTO = AccountStatusDTO(
        username = row[AccountStatus.username],
        statusName = row[AccountStatus.statusName],
        value = row[AccountStatus.value]
    )
}