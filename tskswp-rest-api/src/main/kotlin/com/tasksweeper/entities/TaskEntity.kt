package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tasksweeper.entities.Counter.primaryKey
import org.jetbrains.exposed.sql.Table

object Task : Table("tskswp.task") {
    val id = integer("id").primaryKey()
    val name = varchar("name", 256)
    val startDate = date("start_date")
    val dueDate = date("due_date")
    val difficultyName = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetitionName = varchar("repetition_name", 256).references(Repetition.name)
    val accountName = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512)
}

data class TaskDTO(
    val id : Int,
    val name : String,
    val startDate : String,
    val dueDate : String,
    val difficultyName : String,
    val repetitionName : String,
    val accountName : String,
    val description : String,
)
