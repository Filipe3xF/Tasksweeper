package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object AccountEquipment : Table("tskswp.account_equipment") {
    val username = varchar("username", 256).primaryKey().references(Account.username)
    val equipment_name = varchar("equipment_name", 256).primaryKey().references(Equipment.name)
    val quantity = integer("quantity")
}

data class AccountEquipmentDTO(
    val name: String,
    val equipment_name: String,
    val quantity : Int
)