package com.tasksweeper.repository

import com.tasksweeper.entities.AccountConsumable
import com.tasksweeper.entities.AccountConsumableDTO
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class AccountConsumableRepository {

    suspend fun insertAccountConsumable(
        accountUsername: String,
        accountConsumableId: Long
    ) = transaction {
        AccountConsumable.insert {
            it[username] = accountUsername
            it[consumableId] = accountConsumableId
            it[quantity] = 1
        }
    }.resultedValues?.first()?.let { toAccountConsumable(it) }

    suspend fun increaseQuantity(username: String, consumableId: Long) = transaction {
        AccountConsumable.update({ (AccountConsumable.username eq username) and (AccountConsumable.consumableId eq consumableId) }) {
            with(SqlExpressionBuilder) {
                it[quantity] = quantity + 1
            }
        }
    }

    suspend fun selectAccountConsumable(username: String, consumableId: Long) = transaction {
        AccountConsumable.select {
            (AccountConsumable.username eq username) and (AccountConsumable.consumableId eq consumableId)
        }.single().let { toAccountConsumable(it) }
    }

    private fun toAccountConsumable(row: ResultRow): AccountConsumableDTO = AccountConsumableDTO(
        username = row[AccountConsumable.username],
        consumableId = row[AccountConsumable.consumableId],
        quantity = row[AccountConsumable.quantity],
    )
}
