package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object CounterReminder : Table("tskswp.creminder") {
    val remindDate = timestamp("remind_date")
    val questId = long("quest_id").references(Counter.id)
    override val primaryKey = PrimaryKey(remindDate, questId, name = "creminder_pkey")
}

data class CounterReminderDTO(
    val remindDate: Instant,
    val questId: Long
)
