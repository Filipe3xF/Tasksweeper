package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

enum class Difficulties(val dbName: String) { EASY("Easy"), MEDIUM("Medium"), HARD("Hard") }

object Difficulty : Table("tskswp.difficulty") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "difficulty_pkey")
}

data class DifficultyDTO(
    val name: String,
)
