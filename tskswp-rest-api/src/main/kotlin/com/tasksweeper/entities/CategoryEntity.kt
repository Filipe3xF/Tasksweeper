package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Category : Table("tskswp.category") {
    val name = varchar("name", 256).primaryKey()
}

data class CategoryDTO(
    val name: String
)