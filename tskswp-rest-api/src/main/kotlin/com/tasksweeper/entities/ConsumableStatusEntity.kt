package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.exposed.sql.Table

object ConsumableStatus : Table("tskswp.consumable_status") {
    val consumableName = varchar("consumable_name", 256).primaryKey().references(Consumable.name)
    val statusName = varchar("status_name", 256).primaryKey().references(Status.name)
    val value = integer("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
}

data class ConsumableStatusDTO(
    val consumableName: String,
    val statusName : String,
    val value : Int,
    val percentage : Boolean,
    val instant : Boolean
)
