package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

enum class Repetitions(val dbName: String) { YEAR("Yearly"), MONTH("Monthly"), WEEKLY("Weekly"), DAY("Daily") }

object Repetition : Table("tskswp.repetition") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "repetition_pkey")
}

data class RepetitionDTO(
    val name: String,
)
