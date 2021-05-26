package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

enum class DifficultyMultiplier(val dbName: String, val value: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3)
}
object Difficulty : Table("tskswp.difficulty") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "difficulty_pkey")
}

data class DifficultyDTO(
    val name: String,
)
