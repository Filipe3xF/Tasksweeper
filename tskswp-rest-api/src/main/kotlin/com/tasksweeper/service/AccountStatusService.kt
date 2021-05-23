package com.tasksweeper.service

import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValues
import com.tasksweeper.entities.TaskDTO
import com.tasksweeper.entities.TaskDifficultyValues
import com.tasksweeper.repository.AccountStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountStatusService : KoinComponent {
    private val accountStatusRepository: AccountStatusRepository by inject()

    private fun calculatedReceivedExperience(level: Long, difficultyValue: Int) = 10 + level * difficultyValue

    private fun calculatedReceivedGold(level: Long, difficultyValue: Int) = 10 + level * difficultyValue

    suspend fun insertInitialStatus(accountUsername: String): List<AccountStatusDTO?> {
        val list = mutableListOf<AccountStatusDTO?>()
        for (status in AccountStatusValues.values()) {
            list.add(accountStatusRepository.insertAccountStatus(accountUsername, status.dbName, status.initialValue))
        }
        return list
    }

    suspend fun insertNewStatus(accountUsername: String, level: Long, task : TaskDTO): Long {
        val list = accountStatusRepository.selectAccountStatus(accountUsername)
        val taskDifficultyValue =
            TaskDifficultyValues.values().single { it.dbName == task.difficultyName }.value

        val accumulatedExperience = list.single() {
            it.statusName == "Experience"
        }.value + calculatedReceivedExperience(level, taskDifficultyValue)

        accountStatusRepository.updateStatus(
            accountUsername,
            AccountStatusValues.EXP.dbName,
            accumulatedExperience
        )

        val accumulatedGold =
            list.single { it.statusName == "Gold" }.value + calculatedReceivedGold(level, taskDifficultyValue)

        accountStatusRepository.updateStatus(
            accountUsername,
            AccountStatusValues.GOLD.dbName,
            accumulatedGold
        )

        return level
    }

}