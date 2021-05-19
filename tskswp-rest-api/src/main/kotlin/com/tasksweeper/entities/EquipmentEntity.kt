package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object Equipment : Table("tskswp.equipment") {
    val name = varchar("name", 256)
    val price = integer("price")
    val description = varchar("description", 256)
    val categoryName = varchar("category_name", 256).references(Category.name)
    override val primaryKey = PrimaryKey(name, name = "equipment_pkey")

}

data class EquipmentDTO(
    val name: String,
    val price: Int,
    val description: String,
    val categoryName: String
)
