package com.tasksweeper.repository

import com.tasksweeper.entities.AccountConsumable
import com.tasksweeper.entities.AccountConsumableDTO
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class AccountConsumableRepository {

    suspend fun insertAccountConsumable(
        accountUsername: String,
        accountConsumableName: String,
        consumableQuantity: Long
    ) = transaction {
        AccountConsumable.insert {
            it[username] = accountUsername
            it[consumableName] = accountConsumableName
            it[quantity] = consumableQuantity
        }
    }.resultedValues?.first()?.let { toAccountConsumable(it) }

    suspend fun increaseQuantity(username: String, consumableName: String) = transaction {
        AccountConsumable.update({ (AccountConsumable.username eq username) and (AccountConsumable.consumableName eq consumableName) }) {
            with(SqlExpressionBuilder) {
                it[quantity] = quantity + 1
            }
        }
    }

    suspend fun selectAccountConsumable(username: String, consumableName: String) = transaction {
        AccountConsumable.select {
            (AccountConsumable.username eq username) and (AccountConsumable.consumableName eq consumableName)
        }.single().let { toAccountConsumable(it) }
    }

    private fun toAccountConsumable(row: ResultRow): AccountConsumableDTO = AccountConsumableDTO(
        username = row[AccountConsumable.username],
        consumableName = row[AccountConsumable.consumableName],
        quantity = row[AccountConsumable.quantity],
    )
}
