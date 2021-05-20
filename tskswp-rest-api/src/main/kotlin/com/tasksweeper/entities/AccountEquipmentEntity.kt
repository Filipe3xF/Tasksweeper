package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object AccountEquipment : Table("tskswp.account_equipment") {
    val username = varchar("username", 256).references(Account.username)
    val equipmentName = varchar("equipment_name", 256).references(Equipment.name)
    val quantity = integer("quantity")
    override val primaryKey = PrimaryKey(username, equipmentName, name = "account_equipment_pkey")
}

data class AccountEquipmentDTO(
    val name: String,
    val equipmentName: String,
    val quantity: Int
)
