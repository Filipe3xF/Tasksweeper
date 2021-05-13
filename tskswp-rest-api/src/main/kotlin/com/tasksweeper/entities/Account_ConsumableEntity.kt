package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Account_Consumable : Table("tskswp.account_consumable") {
    val username = varchar("username", 256).primaryKey().references(Account.username)
    val consumable_name = varchar("consumable_name", 256).primaryKey().references(Consumable.name)
    val quantity = integer("quantity")
}

data class Account_ConsumableDTO(
    val username: String,
    val consumable_name: String,
    val quantity : Int
)
