package com.tasksweeper.repository

import com.tasksweeper.entities.*
import com.tasksweeper.exceptions.RegisterException
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.*

class StatusRepository {
    suspend fun selectStatus(name: String) = transaction {
        Status.select {
            Status.name eq name
        }.single().let { toStatus(it) }
    }

    suspend fun selectAllStatus() = transaction {
        Status.selectAll().let { toStatusList(it) }
    }

    private fun toStatusList(query: Query): List<StatusDTO> {

        val list = mutableListOf<StatusDTO>();
        query.forEach { list.add(toStatus(it)) }
        return list;
    }

    private fun toStatus(row: ResultRow): StatusDTO = StatusDTO(
        name = row[Status.name]
    )
}