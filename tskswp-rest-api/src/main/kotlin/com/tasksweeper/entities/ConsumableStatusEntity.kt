package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object ConsumableStatus : Table("tskswp.consumable_status") {
    val consumable_name = varchar("consumable_name", 256).primaryKey().references(Consumable.name)
    val status_name = varchar("status_name", 256).primaryKey().references(Status.name)
    val value = integer("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
}

data class ConsumableStatusDTO(
    val consumable_name: String,
    val status_name : String,
    val value : Int,
    val percentage : Boolean,
    val instant : Boolean
)
