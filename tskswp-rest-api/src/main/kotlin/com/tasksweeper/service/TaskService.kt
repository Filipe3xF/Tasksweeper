package com.tasksweeper.service

import com.tasksweeper.controller.DateDTO
import com.tasksweeper.controller.TimeDTO
import com.tasksweeper.entities.Difficulties
import com.tasksweeper.entities.Repetitions
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.entities.TaskDifficulty
import com.tasksweeper.exceptions.InvalidDifficultyException
import com.tasksweeper.exceptions.InvalidDueDateException
import com.tasksweeper.exceptions.InvalidRepetitionException
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.utils.instantOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant


class TaskService : KoinComponent {
    private val accountStatusService: AccountStatusService by inject()
    private val accountService: AccountService by inject()
    private val taskRepository: TaskRepository by inject()

    suspend fun createTask(
        taskName: String,
        taskDueDate: DateDTO?,
        taskDueTime: TimeDTO?,
        taskDifficultyName: String,
        taskRepetition: String?,
        taskAccountName: String,
        taskDescription: String?
    ): TaskDTO {

        val dueInstant: Instant? = taskDueDate?.let { date ->
            taskDueTime?.let { time ->
                instantOf(date, time)
            }
        }

        val taskStartDay = Instant.now()

        if (Difficulties.values().none { it.dbName == taskDifficultyName })
            throw InvalidDifficultyException(taskDifficultyName)

        if (taskRepetition != null && Repetitions.values().none { it.dbName == taskRepetition })
            throw InvalidRepetitionException(taskRepetition)

        if (dueInstant != null && taskStartDay.isAfter(dueInstant))
            throw InvalidDueDateException(dueInstant.toString())

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

    suspend fun closeTaskSuccessfully(username: String, taskId: Long): Any {
        accountStatusService.reward(
            accountService.getAccount(username),
            TaskDifficulty.values().single { taskRepository.selectTask(taskId).difficultyName == it.dbName }
        )
        taskRepository.deleteTask(taskId)
        return "Task with id: ${taskId} was deleted successfully"
    }
}
