package com.tasksweeper.entities

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Counter : LongIdTable("tskswp.counter") {
    val name = varchar("name", 256)
    val objective = varchar("objective", 256)
    val value = long("value")
    val positive = bool("positive")
    val difficultyName = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetitionName = varchar("repetition_name", 256).references(Repetition.name).nullable()
    val accountName = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512).nullable()
    val startDate = timestamp("start_date")
}

data class CounterDTO(
    val id: Long,
    val name: String,
    val objective: String,
    val value: Long,
    val positive: Boolean,
    val difficultyName: String,
    val repetitionName: String?,
    val accountName: String,
    val description: String?,
    val startDate: Instant
)
