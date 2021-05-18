package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object AccountConsumable : Table("tskswp.account_consumable") {
    val username = varchar("username", 256).primaryKey().references(Account.username)
    val consumableName = varchar("consumable_name", 256).primaryKey().references(Consumable.name)
    val quantity = integer("quantity")
}

data class AccountConsumableDTO(
    val username: String,
    val consumableName: String,
    val quantity : Int
)