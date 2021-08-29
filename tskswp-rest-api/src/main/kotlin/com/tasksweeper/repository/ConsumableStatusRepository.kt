package com.tasksweeper.repository

import com.tasksweeper.entities.ConsumableStatus
import com.tasksweeper.entities.ConsumableStatusDTO
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ConsumableStatusRepository {

    suspend fun selectConsumableStatus(consumableId: Long) = transaction {
        ConsumableStatus.select { ConsumableStatus.consumableId eq consumableId }
            .let {
                it.map { row: ResultRow -> toConsumableStatus(row) }
            }
    }

    private fun toConsumableStatus(row: ResultRow): ConsumableStatusDTO = ConsumableStatusDTO(
        consumableId = row[ConsumableStatus.consumableId],
        statusName = row[ConsumableStatus.statusName],
        value = row[ConsumableStatus.value],
        percentage = row[ConsumableStatus.percentage],
        instant = row[ConsumableStatus.instant]
    )
}
