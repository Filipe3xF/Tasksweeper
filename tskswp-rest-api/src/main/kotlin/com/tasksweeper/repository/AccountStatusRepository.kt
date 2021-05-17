package com.tasksweeper.repository

import com.tasksweeper.entities.*
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class AccountStatusRepository {


    suspend fun deleteAccountStatus(accountname: String) = transaction {
        AccountStatus.deleteWhere {
            AccountStatus.username eq accountname
        }
    }


    suspend fun insertAccountStatus(accountname: String, statusname: String, parameter: Int) = transaction {
        AccountStatus.insert {
            it[username] = accountname
            it[status_name] = statusname
            it[value] = parameter
        }.resultedValues?.first()?.let { toAccountStatus(it) }
    }

    suspend fun selectAccountStatus(username: String) = transaction {
        AccountStatus.select {
            AccountStatus.username eq username
        }.let { toAccountStatusList(it) }
    }

    private fun toAccountStatusList(query: Query): List<AccountStatusDTO> {

        val list = mutableListOf<AccountStatusDTO>();
        query.forEach { list.add(toAccountStatus(it)) }
        return list;
    }

    private fun toAccountStatus(row: ResultRow): AccountStatusDTO = AccountStatusDTO(
        username = row[AccountStatus.username],
        status_name = row[AccountStatus.status_name],
        value = row[AccountStatus.value]
    )
}