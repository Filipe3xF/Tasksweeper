package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object AccountStatus : Table("tskswp.account_status") {
    val username = varchar("username", 256).references(Account.username)
    val statusName = varchar("status_name", 256).references(Status.name)
    val value = integer("value")
    override val primaryKey = PrimaryKey(username, statusName, name = "account_status_pkey")
}

data class AccountStatusDTO(
    val username: String,
    val statusName: String,
    val value: Int
)
