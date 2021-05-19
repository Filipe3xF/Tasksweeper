package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object AccountConsumable : Table("tskswp.account_consumable") {
    val username = varchar("username", 256).references(Account.username)
    val consumableName = varchar("consumable_name", 256).references(Consumable.name)
    val quantity = integer("quantity")
    override val primaryKey = PrimaryKey(username, consumableName, name = "account_consumable_pkey")
}

data class AccountConsumableDTO(
    val username: String,
    val consumableName: String,
    val quantity: Int
)
