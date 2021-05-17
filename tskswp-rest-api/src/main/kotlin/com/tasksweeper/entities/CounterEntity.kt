package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tasksweeper.entities.Counter.primaryKey
import org.jetbrains.exposed.sql.Table

object Counter : Table("tskswp.counter") {
    val id = integer("id").primaryKey()
    val name = varchar("name", 256)
    val objective = varchar("objective", 256)
    val value = integer("value")
    val positive = bool("positive")
    val difficulty_name = varchar("difficulty_name", 256).references(Difficulty.name)
    val repetition_name = varchar("repetition_name", 256).references(Repetition.name)
    val account_name = varchar("account_name", 256).references(Account.username)
    val description = varchar("description", 512)
    val due_date = date("due_date")
}

data class CounterDTO(
    val id : Int,
    val name : String,
    val objective : String,
    val value : Int,
    val positive : Boolean,
    val difficulty_name : String,
    val repetition_name : String,
    val account_name : String,
    val description : String,
    val due_date : String
)