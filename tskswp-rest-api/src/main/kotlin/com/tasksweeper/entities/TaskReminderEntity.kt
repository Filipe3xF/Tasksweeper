package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object TaskReminder : Table("tskswp.treminder") {
    val remindDate = date("remind_date").primaryKey()
    val questId = integer("quest_id").references(Task.id)
}

data class TaskReminderDTO(
    val remindDate: String,
    val questId : Int
)
