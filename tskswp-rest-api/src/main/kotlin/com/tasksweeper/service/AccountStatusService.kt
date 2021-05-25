package com.tasksweeper.service

import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue
import com.tasksweeper.entities.AccountStatusValue.EXP
import com.tasksweeper.entities.AccountStatusValue.GOLD
import com.tasksweeper.entities.TaskDifficulty
import com.tasksweeper.repository.AccountStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.pow

class AccountStatusService : KoinComponent {
    private val accountStatusRepository: AccountStatusRepository by inject()

    private fun calculateExperienceGain(level: Long, difficulty: Int) =
        (10 + level.toDouble().pow(1.45) * difficulty).toLong()

    private fun calculateGoldGain(level: Long, difficulty: Int) = 10 + level * difficulty

    suspend fun insertInitialStatus(accountUsername: String): List<AccountStatusDTO?> {
        val list = mutableListOf<AccountStatusDTO?>()
        for (status in AccountStatusValue.values()) {
            list.add(accountStatusRepository.insertAccountStatus(accountUsername, status.dbName, status.initialValue))
        }
        return list
    }

    suspend fun reward(account: AccountDTO, taskDifficulty: TaskDifficulty) {
        accountStatusRepository.selectAccountStatus(account.username).let { statusList ->
            statusList.updateStatusValue(EXP, calculateExperienceGain(account.level, taskDifficulty.value))
            statusList.updateStatusValue(GOLD, calculateGoldGain(account.level, taskDifficulty.value))
        }
    }

    private suspend fun List<AccountStatusDTO>.updateStatusValue(accountStatusValue: AccountStatusValue, valueDelta: Long) =
        single { it.statusName == accountStatusValue.dbName }.let {
            accountStatusRepository.updateStatus(
                it.username,
                it.statusName,
                it.value + valueDelta
            )
        }
}
