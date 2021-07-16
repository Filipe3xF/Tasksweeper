package com.tasksweeper.entities

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.tasksweeper.utils.InstantDeserializer
import com.tasksweeper.utils.InstantSerializer
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant


object Task : LongIdTable("tskswp.task") {
    val name = varchar("name", 256)
    val startDate = timestamp("start_date")
    val dueDate = timestamp("due_date").nullable()
    val difficultyName = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetitionName = varchar("repetition_name", 256).references(Repetition.name).nullable()
    val accountName = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512).nullable()
    val state = varchar("state", 256).references(TaskState.name)
}

data class TaskDTO(
    val id: Long,
    val name: String,
    @JsonSerialize(using = InstantSerializer::class)
    @JsonDeserialize(using = InstantDeserializer::class)
    val startDate: Instant,
    @JsonSerialize(using = InstantSerializer::class)
    @JsonDeserialize(using = InstantDeserializer::class)
    val dueDate: Instant?,
    val difficultyName: String,
    val repetitionName: String?,
    val accountName: String,
    val description: String?,
    val state: String
)
