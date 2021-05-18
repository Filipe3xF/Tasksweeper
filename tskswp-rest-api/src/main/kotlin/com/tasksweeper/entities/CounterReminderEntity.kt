package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object CounterReminder : Table("tskswp.creminder") {
    val remindDate = date("remind_date").primaryKey()
    val questId = integer("quest_id").references(Counter.id)
}

data class CounterReminderDTO(
    val remindDate: String,
    val questId : Int
)
