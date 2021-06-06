package com.tasksweeper.entities

import org.jetbrains.exposed.sql.Table


enum class TaskStateValue(val dbName: String) {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    FAILED("Failed")
}

object TaskState : Table("tskswp.task_state") {
    val name = varchar("name", 256)
    override val primaryKey = PrimaryKey(name, name = "task_state_pkey")
}

data class TaskStateDTO(
    val name: String,
)
