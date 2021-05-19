package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tasksweeper.entities.Counter.primaryKey
import org.jetbrains.exposed.sql.Table
import java.time.OffsetDateTime

object Counter : Table("tskswp.counter") {
    val id = integer("id").primaryKey()
    val name = varchar("name", 256)
    val objective = varchar("objective", 256)
    val value = integer("value")
    val positive = bool("positive")
    val difficultyName = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetitionName = varchar("repetition_name", 256).references(Repetition.name).nullable()
    val accountName = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512).nullable()
    val dueDate = date("due_date").nullable()
}

data class CounterDTO(
    val id : Int,
    val name : String,
    val objective : String,
    val value : Int,
    val positive : Boolean,
    val difficultyName : String,
    val repetitionName : String?,
    val accountName : String,
    val description : String?,
    val dueDate : OffsetDateTime?
)