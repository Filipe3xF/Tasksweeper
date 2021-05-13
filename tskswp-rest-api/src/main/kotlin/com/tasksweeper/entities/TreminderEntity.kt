package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object TReminder : Table("tskswp.treminder") {
    val remind_date = date("remind_date").primaryKey()
    val quest_id = integer("quest_id").references(Task.id)
}

data class TReminderDTO(
    val remind_date: String,
    val quest_id : Int
)
