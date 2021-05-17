package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Equipment : Table("tskswp.equipment") {
    val name = varchar("name", 256).primaryKey()
    val price = integer("price")
    val description = varchar("description", 256)
    val category_name = varchar("category_name", 256).references(Category.name)
}

data class EquipmentDTO(
    val name: String,
    val price: Int,
    val description : String,
    val category_name : String
)