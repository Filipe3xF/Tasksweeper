package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object Consumable : Table("tskswp.consumable") {
    val name = varchar("name", 256)
    val price = integer("price")
    val description = varchar("description", 256)
    override val primaryKey = PrimaryKey(name, name = "consumable_pkey")
}

data class ConsumableDTO(
    val name: String,
    val price: Int,
    val description: String
)
