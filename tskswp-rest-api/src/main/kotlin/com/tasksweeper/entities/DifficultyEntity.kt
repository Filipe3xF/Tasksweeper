package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Difficulty : Table("tskswp.difficulty") {
    val name = varchar("name", 256).primaryKey()
}

data class DifficultyDTO(
    val name: String,
)
