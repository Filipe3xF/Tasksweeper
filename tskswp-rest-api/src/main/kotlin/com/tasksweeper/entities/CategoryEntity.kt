package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object Category : Table("tskswp.category") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "category_pkey")
}

data class CategoryDTO(
    val name: String
)
