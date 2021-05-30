package com.tasksweeper.repository

import com.tasksweeper.entities.Task
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.repository.DatabaseFactory.transaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant

class TaskRepository {

    suspend fun insertTask(
        taskName: String, taskStartDate: Instant,
        taskDueDate: Instant?, taskDifficultyName: String,
        taskRepetition: String?, taskAccountName: String,
        taskDescription: String?
    ) = transaction {
        Task.insert {
            it[name] = taskName
            it[startDate] = taskStartDate
            it[dueDate] = taskDueDate
            it[difficultyName] = taskDifficultyName
            it[repetitionName] = taskRepetition
            it[accountName] = taskAccountName
            it[description] = taskDescription
        }.resultedValues?.first()?.let { toTask(it) } ?: throw DatabaseNotFoundException("Task")
    }

    suspend fun selectTask(taskId: Long) = transaction {
        Task.select {
            Task.id eq taskId
        }.single().let { toTask(it) }
    }

    private fun toTask(row: ResultRow) = TaskDTO(
        id = row[Task.id].value,
        name = row[Task.name],
        startDate = row[Task.startDate],
        dueDate = row[Task.dueDate],
        difficultyName = row[Task.difficultyName],
        repetitionName = row[Task.repetitionName],
        accountName = row[Task.accountName],
        description = row[Task.description]
    )

    suspend fun deleteTask(taskId: Long) = transaction {
        Task.deleteWhere {
            Task.id eq taskId
        }
    }
}
