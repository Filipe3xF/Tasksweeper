package com.tasksweeper.service

import com.tasksweeper.repository.AccountRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RewardService : KoinComponent {
    private val accountStatusService : AccountStatusService by inject()
    private val accountService : AccountService by inject()
    private val taskService : TaskService by inject()

    suspend fun giveReward(accountUsername : String, taskId: Long) : String{
        val level = accountService.getAccount(accountUsername).level
        accountStatusService.insertNewStatus(accountUsername, level, taskId)
        taskService.deleteTask(taskId)
        return "Task with id: ${taskId} was deleted successfully"
    }

}