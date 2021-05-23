package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object ConsumableStatus : Table("tskswp.consumable_status") {
    val consumableName = varchar("consumable_name", 256).references(Consumable.name)
    val statusName = varchar("status_name", 256).references(Status.name)
    val value = long("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
    override val primaryKey = PrimaryKey(consumableName, statusName, name = "consumable_status_pkey")
}

data class ConsumableStatusDTO(
    val consumableName: String,
    val statusName: String,
    val value: Long,
    val percentage: Boolean,
    val instant: Boolean
)
