package com.tasksweeper.service

import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.exceptions.InvalidDifficultyException
import com.tasksweeper.exceptions.InvalidDueDateException
import com.tasksweeper.exceptions.InvalidRepetitionException
import com.tasksweeper.repository.TaskRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField


class TaskService : KoinComponent {

    val HOURS : Long = 23
    val MINUTES : Long = 59
    val SECONDS : Long = 59

    private val difficultyList: List<String> = listOf("Easy", "Medium", "Hard")
    private val repetitionList: List<String> = listOf("Daily", "Weekly", "Monthly", "Yearly")

    val taskRepository: TaskRepository by inject()

    val formatter = DateTimeFormatterBuilder()
        .appendPattern("dd-MM-yyyy")
        .parseDefaulting(ChronoField.HOUR_OF_DAY, HOURS)
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, MINUTES)
        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, SECONDS)
        .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
        .toFormatter()
        .withZone(ZoneId.systemDefault())


    suspend fun createTask(
        taskName: String,
        taskStartDay: Instant,
        taskDueDate: String?,
        taskDifficultyName: String,
        taskRepetition: String?,
        taskAccountName: String,
        taskDescription: String?
    ): TaskDTO {

        var dueInstant: Instant? = null

        if (taskDueDate != null)
            dueInstant = formatter.parse(taskDueDate, Instant :: from)

        if (!difficultyList.contains(taskDifficultyName))
            throw InvalidDifficultyException(taskDifficultyName)

        if (taskRepetition != null && !repetitionList.contains(taskRepetition))
            throw InvalidRepetitionException(taskRepetition)

        if (dueInstant != null && taskStartDay.isAfter(dueInstant))
            throw InvalidDueDateException(taskDueDate)

        return taskRepository.insertTask(
            taskName,
            taskStartDay,
            dueInstant,
            taskDifficultyName,
            taskRepetition,
            taskAccountName,
            taskDescription
        )
    }
}