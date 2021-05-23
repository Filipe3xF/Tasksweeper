package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object TaskReminder : Table("tskswp.treminder") {
    val remindDate = timestamp("remind_date")
    val questId = long("quest_id").references(Task.id)
    override val primaryKey = PrimaryKey(remindDate, questId, name = "treminder_pkey")
}

data class TaskReminderDTO(
    val remindDate: Instant,
    val questId: Long
)
