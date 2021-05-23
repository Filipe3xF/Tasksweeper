package com.tasksweeper.repository

import com.tasksweeper.entities.Status
import com.tasksweeper.entities.StatusDTO
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class StatusRepository {
    suspend fun selectStatus(name: String) = transaction {
        Status.select {
            Status.name eq name
        }.single().let { toStatus(it) }
    }

    suspend fun selectAllStatus() = transaction {
        toStatusList(Status.selectAll())
    }

    private fun toStatusList(query: Query): List<StatusDTO> {
        val list = mutableListOf<StatusDTO>()
        query.forEach { list.add(toStatus(it)) }
        return list
    }

    private fun toStatus(row: ResultRow): StatusDTO = StatusDTO(
        name = row[Status.name]
    )
}