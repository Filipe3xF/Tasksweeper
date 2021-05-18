package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object AccountEquipment : Table("tskswp.account_equipment") {
    val username = varchar("username", 256).primaryKey().references(Account.username)
    val equipmentName = varchar("equipment_name", 256).primaryKey().references(Equipment.name)
    val quantity = integer("quantity")
}

data class AccountEquipmentDTO(
    val name: String,
    val equipmentName: String,
    val quantity : Int
)