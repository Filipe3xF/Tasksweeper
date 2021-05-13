package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Repetition : Table("tskswp.repetition") {
    val name = varchar("name", 256).primaryKey()
}

data class RepetitionDTO(
    val name: String,
)
