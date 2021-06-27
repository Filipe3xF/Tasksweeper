package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object Consumable : Table("tskswp.consumable") {
    val id = long("id")
    val name = varchar("name", 256)
    val price = integer("price")
    val description = varchar("description", 256)
    override val primaryKey = PrimaryKey(id, name = "consumable_pkey")
}

data class ConsumableDTO(
    val id: Long,
    val name: String,
    val price: Int,
    val description: String
)
