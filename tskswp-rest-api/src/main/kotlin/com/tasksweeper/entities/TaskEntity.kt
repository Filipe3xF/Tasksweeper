package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tasksweeper.entities.Counter.primaryKey
import org.jetbrains.exposed.sql.Table

object Task : Table("tskswp.task") {
    val id = integer("id").primaryKey()
    val name = varchar("name", 256)
    val start_date = date("start_date")
    val due_date = date("due_date")
    val difficulty_name = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetition_name = varchar("repetition_name", 256).references(Repetition.name)
    val account_name = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512)
}

data class TaskDTO(
    val id : Int,
    val name : String,
    val start_date : String,
    val due_date : String,
    val difficulty_name : String,
    val repetition_name : String,
    val account_name : String,
    val description : String,
)
