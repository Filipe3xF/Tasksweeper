package com.tasksweeper.repository

import com.tasksweeper.entities.Consumable
import com.tasksweeper.entities.ConsumableDTO
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import com.tasksweeper.repository.DatabaseFactory.transaction

class ConsumableRepository {

    suspend fun selectConsumable(consumableId: Long) = transaction {
        Consumable.select {
            Consumable.id eq consumableId
        }.single().let { toConsumable(it) }
    }

    private fun toConsumable(row: ResultRow): ConsumableDTO = ConsumableDTO(
        id = row[Consumable.id],
        name = row[Consumable.name],
        price = row[Consumable.price],
        description = row[Consumable.description]
    )

}