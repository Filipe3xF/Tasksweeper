package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object Repetition : Table("tskswp.repetition") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "repetition_pkey")
}

data class RepetitionDTO(
    val name: String,
)
