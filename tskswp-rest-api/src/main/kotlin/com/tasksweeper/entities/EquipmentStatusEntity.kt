package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table

object EquipmentStatus : Table("tskswp.equipment_status") {
    val equipmentName = varchar("equipment_name", 256).references(Equipment.name)
    val statusName = varchar("status_name", 256).references(Status.name)
    val value = long("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
    override val primaryKey = PrimaryKey(equipmentName, statusName, name = "equipment_status_pkey")
}

data class EquipmentStatusDTO(
    val equipmentName: String,
    val statusName: String,
    val value: Long,
    val percentage: Boolean,
    val instant: Boolean
)
