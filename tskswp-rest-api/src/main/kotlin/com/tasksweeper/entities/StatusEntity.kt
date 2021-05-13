package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Status : Table("tskswp.status") {
    val name = varchar("name", 256).primaryKey()
}

data class StatusDTO(
    val name: String,
)
