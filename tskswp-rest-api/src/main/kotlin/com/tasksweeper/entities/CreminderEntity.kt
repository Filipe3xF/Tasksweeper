package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object CReminder : Table("tskswp.creminder") {
    val remind_date = date("remind_date").primaryKey()
    val quest_id = integer("quest_id").references(Counter.id)
}

data class CReminderDTO(
    val remind_date: String,
    val quest_id : Int
)
