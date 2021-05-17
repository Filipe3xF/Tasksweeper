package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object Account : Table("tskswp.account") {
    val username = varchar("username", 256).primaryKey()
    val email = varchar("email", 256)
    val password = varchar("password", 256)
    val level = integer("level")
}

data class AccountDTO(
    val username: String,
    val email: String,
    @JsonIgnore val password: String,
    val level: Int
)