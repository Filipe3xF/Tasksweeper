package com.tasksweeper.repository

import com.tasksweeper.entities.*
import com.tasksweeper.exceptions.RegisterException
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class Account_StatusRepository {



    suspend fun insertAccount_Status(accountname : String, statusname : String, parameter: Int) = transaction{
        Account_Status.insert {
            it[username] = accountname
            it[status_name] = statusname
            it[value] = parameter
        }.resultedValues?.first()?.let { toAccount_Status(it)}
    }



    suspend fun selectAllAccount_Status(username: String) = transaction {
        Account_Status.select {
            Account_Status.username eq username
        }.let { toAccount_StatusList(it)}
    }

    private fun toAccount_StatusList(query: Query): List<Account_StatusDTO> {

        val list = mutableListOf<Account_StatusDTO>();
        query.forEach { list.add(toAccount_Status(it)) }
        return list;
    }

    private fun toAccount_Status(row: ResultRow): Account_StatusDTO = Account_StatusDTO(
        username = row[Account_Status.username],
        status_name = row[Account_Status.status_name],
        value = row[Account_Status.value]
    )



}
