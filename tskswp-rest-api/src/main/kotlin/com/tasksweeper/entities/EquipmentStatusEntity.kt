package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jdk.jfr.Percentage
import org.jetbrains.exposed.sql.Table

object EquipmentStatus : Table("tskswp.equipment_status") {
    val equipmentName = varchar("equipment_name", 256).primaryKey().references(Equipment.name)
    val statusName = varchar("status_name", 256).primaryKey().references(Status.name)
    val value = integer("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
}

data class EquipmentStatusDTO(
    val equipmentName: String,
    val statusName: String,
    val value : Int,
    val percentage: Boolean,
    val instant : Boolean
)
