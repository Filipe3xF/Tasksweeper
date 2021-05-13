package com.tasksweeper.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jdk.jfr.Percentage
import org.jetbrains.exposed.sql.Table

object Equipment_Status : Table("tskswp.equipment_status") {
    val equipment_name = varchar("equipment_name", 256).primaryKey().references(Equipment.name)
    val status_name = varchar("status_name", 256).primaryKey().references(Status.name)
    val value = integer("value")
    val percentage = bool("percentage")
    val instant = bool("instant")
}

data class Equipment_StatusDTO(
    val equipment_name: String,
    val status_name: String,
    val value : Int,
    val percentage: Boolean,
    val instant : Boolean
)
