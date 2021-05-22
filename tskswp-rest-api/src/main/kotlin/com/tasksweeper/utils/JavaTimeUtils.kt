package com.tasksweeper.utils

import com.tasksweeper.controller.DateDTO
import com.tasksweeper.controller.TimeDTO
import java.time.Instant


fun instantOf(date: DateDTO, time: TimeDTO) : Instant? = Instant.parse("${date.year}-${date.month}-${date.day}T${time.hour}:${time.minute}:${time.second}.000Z")
