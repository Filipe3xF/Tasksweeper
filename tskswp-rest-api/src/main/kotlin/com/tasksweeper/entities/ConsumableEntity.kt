package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Consumable : Table("tskswp.consumable") {
    val name = varchar("name", 256).primaryKey()
    val price = integer("price")
    val description = varchar("description", 256)
}

data class ConsumableDTO(
    val name: String,
    val price : Int,
    val description: String
)
