package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

enum class Stats(val dbName: String) { HP("Health"), EXP("Experience"), GOLD("Gold") }


object Status : Table("tskswp.status") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(Repetition.name, name = "status_pkey")
}

data class StatusDTO(
    val name: String,
)
