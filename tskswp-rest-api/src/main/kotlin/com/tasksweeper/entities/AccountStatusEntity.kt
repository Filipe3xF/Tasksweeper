package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object AccountStatus : Table("tskswp.account_status") {
    val username = varchar("username", 256).primaryKey().references(Account.username)
    val statusName = varchar("status_name", 256).primaryKey().references(Status.name)
    val value = integer("value")
}

data class AccountStatusDTO(
    val username: String,
    val statusName: String,
    val value: Int
)