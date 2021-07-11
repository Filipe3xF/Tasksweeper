package com.tasksweeper.service

import com.tasksweeper.controller.DateDTO
import com.tasksweeper.controller.TimeDTO
import com.tasksweeper.entities.*
import com.tasksweeper.entities.TaskStateValue.*
import com.tasksweeper.exceptions.*
import com.tasksweeper.repository.TaskRepository
import com.tasksweeper.utils.instantOf
import com.tasksweeper.utils.removeSpaces
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant


class TaskService : KoinComponent {
    private val accountStatusService: AccountStatusService by inject()
    private val accountService: AccountService by inject()
    private val taskRepository: TaskRepository by inject()

    private val openedStatus = listOf(TO_DO, IN_PROGRESS)
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

    suspend fun getOpenUserTasks(accountUsername: String, state: String?) =
        when (val stateValue = state?.lowercase()?.removeSpaces()) {
            "open" -> openedStatus
            "closed" -> closedStatus
            null -> TaskStateValue.values().toList()
            else -> TaskStateValue.values().filter { it.dbName.lowercase().removeSpaces() == stateValue }
        }.let {
            val taskList = taskRepository.getUserTasksWithStatus(accountUsername, it)
            TasksDTO(taskList.size, taskList)
        }

    suspend fun completeTaskSuccessfully(accountUsername: String, taskId: Long) =
        completeTask(accountUsername, taskId, DONE, accountStatusService::reward)

    suspend fun completeTaskUnsuccessfully(accountUsername: String, taskId: Long) =
        completeTask(accountUsername, taskId, FAILED, accountStatusService::punish)

    private suspend fun completeTask(
        accountUsername: String,
        taskId: Long,
        taskStatus: TaskStateValue,
        accountStatusAction: suspend (account: AccountDTO, difficulty: DifficultyMultiplier) -> Unit
    ): TaskDTO {
        val task = getTask(taskId)

        if (task.accountName != accountUsername)
            throw NotAuthorizedTaskCompletionException(accountUsername)

        task.checkIfItIsClosed()

        taskRepository.updateTaskState(task.id, taskStatus)

        val account = accountService.getAccount(accountUsername)
        val difficulty = DifficultyMultiplier.valueOf(task.difficultyName.uppercase())

        accountStatusAction(account, difficulty)

        return task.copy(state = taskStatus.dbName)
    }

    private suspend fun getTask(taskId: Long) = taskRepository.selectTask(taskId)

    private fun TaskDTO.checkIfItIsClosed() {
        if (closedStatus.any { it.dbName == state })
            throw TaskAlreadyClosedException(id)
    }
}

data class TasksDTO(val length: Int,val tasks: List<TaskDTO>)
