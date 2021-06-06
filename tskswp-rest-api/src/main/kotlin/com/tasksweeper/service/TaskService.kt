package com.tasksweeper.service

import com.tasksweeper.controller.DateDTO
import com.tasksweeper.controller.TimeDTO
import com.tasksweeper.entities.DifficultyMultiplier
import com.tasksweeper.entities.Repetitions
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.entities.TaskStateValue.*
import com.tasksweeper.exceptions.*
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.utils.instantOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant


class TaskService : KoinComponent {
    private val accountStatusService: AccountStatusService by inject()
    private val accountService: AccountService by inject()
    private val taskRepository: TaskRepository by inject()

    private val closedStatus = listOf(DONE, FAILED)

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

        if (DifficultyMultiplier.values().none { it.dbName == taskDifficultyName })
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
            taskDescription,
            TO_DO
        )
    }

    suspend fun deleteTask(accountUsername: String, taskId: Long): TaskDTO = getTask(taskId).also {
        if (it.accountName != accountUsername)
            throw NotAuthorizedTaskDeletionException(accountUsername)

        taskRepository.deleteTask(taskId)
    }

    suspend fun completeTaskSuccessfully(accountUsername: String, taskId: Long): TaskDTO {
        val task = getTask(taskId)

        if (task.accountName != accountUsername)
            throw NotAuthorizedTaskCompletionException(accountUsername)

        task.checkIfItIsClosed()

        taskRepository.updateTaskState(task.id, DONE)

        val account = accountService.getAccount(accountUsername)
        val difficulty = DifficultyMultiplier.valueOf(task.difficultyName.uppercase())

        accountStatusService.reward(
            account,
            difficulty
        )

        return getTask(taskId)
    }

    suspend fun completeTaskUnsuccessfully(accountUsername: String, taskId: Long): TaskDTO {
        val task = getTask(taskId)

        if (task.accountName != accountUsername)
            throw NotAuthorizedTaskCompletionException(accountUsername)

        task.checkIfItIsClosed()

        taskRepository.updateTaskState(task.id, FAILED)

        val account = accountService.getAccount(accountUsername)
        val difficulty = DifficultyMultiplier.valueOf(task.difficultyName.uppercase())

        accountStatusService.punish(
            account,
            difficulty
        )

        return getTask(taskId)
    }

    private suspend fun getTask(taskId: Long) = taskRepository.selectTask(taskId)

    private fun TaskDTO.checkIfItIsClosed() {
        if (closedStatus.any { it.dbName == state })
            throw TaskAlreadyClosedException(id)
    }
}
