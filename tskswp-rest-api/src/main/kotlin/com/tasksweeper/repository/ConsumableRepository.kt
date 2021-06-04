package com.tasksweeper.repository

import com.tasksweeper.entities.Account
import com.tasksweeper.entities.Consumable
import com.tasksweeper.entities.ConsumableDTO
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ConsumableRepository {

    suspend fun selectConsumable(id: Long) = transaction{
        Consumable.select {
            Consumable.id eq id
        }.single().let { toConsumable(it) }
    }

    private fun toConsumable(row: ResultRow) : ConsumableDTO = ConsumableDTO(
        id = row[Consumable.id],
        name = row[Consumable.name],
        price = row[Consumable.price],
        description = row[Consumable.description]
    )

}