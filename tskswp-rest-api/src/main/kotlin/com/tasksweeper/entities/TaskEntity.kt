package com.tasksweeper.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Task : IntIdTable("tskswp.task") {
    val name = varchar("name", 256)
    val startDate = timestamp("start_date")
    val dueDate = timestamp("due_date").nullable()
    val difficultyName = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetitionName = varchar("repetition_name", 256).references(Repetition.name).nullable()
    val accountName = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512).nullable()
}

data class TaskDTO(
    val id: Int,
    val name: String,
    val startDate: Instant,
    val dueDate: Instant?,
    val difficultyName: String,
    val repetitionName: String?,
    val accountName: String,
    val description: String?,
)
