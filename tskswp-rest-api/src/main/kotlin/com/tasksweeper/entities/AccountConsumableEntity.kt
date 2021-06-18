package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object AccountConsumable : Table("tskswp.account_consumable") {
    val username = varchar("username", 256).references(Account.username)
    val consumableId = long("consumable_id").references(Consumable.id)
    val quantity = long("quantity")
    override val primaryKey = PrimaryKey(username, consumableId, name = "account_consumable_pkey")
}

data class AccountConsumableDTO(
    val username: String,
    val consumableId: Long,
    val quantity: Long
)
