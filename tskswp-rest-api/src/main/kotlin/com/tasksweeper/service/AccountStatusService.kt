package com.tasksweeper.service

import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue
import com.tasksweeper.entities.AccountStatusValue.*
import com.tasksweeper.entities.DifficultyMultiplier
import com.tasksweeper.repository.AccountStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.pow

class AccountStatusService : KoinComponent {
    private val accountStatusRepository: AccountStatusRepository by inject()
    private val accountService: AccountService by inject()

    private fun calculateExperienceGain(level: Long, difficulty: Int) =
        (10 + level.toDouble().pow(1.45) * difficulty).toLong()

    private fun calculateGoldGain(level: Long, difficulty: Int) = 10 + level * difficulty

    private fun calculateHealthLoss(level: Long, difficulty: Int) =
        (-1) * (20 + ((level.toDouble().pow(1.8)) / difficulty)).toLong()

    private fun calculateGoldLost(gold: Long) = gold - (gold * 0.10).toLong()

    private fun calculateMaxHealth(level: Long) = 100 + (20 * (level - 1))

    suspend fun insertInitialStatus(accountUsername: String): List<AccountStatusDTO?> {
        val list = mutableListOf<AccountStatusDTO?>()
        for (status in AccountStatusValue.values()) {
            list.add(accountStatusRepository.insertAccountStatus(accountUsername, status.dbName, status.initialValue))
        }
        return list
    }

    suspend fun reward(account: AccountDTO, difficultyMultiplier: DifficultyMultiplier) {
        accountStatusRepository.selectAccountStatus(account.username).let { statusList ->
            statusList.updateStatusValue(EXP, calculateExperienceGain(account.level, difficultyMultiplier.value))
            statusList.updateStatusValue(GOLD, calculateGoldGain(account.level, difficultyMultiplier.value))
        }
    }

    suspend fun punish(account: AccountDTO, difficultyMultiplier: DifficultyMultiplier) {
        val accountStatusList = accountStatusRepository.selectAccountStatus(account.username)
        val currentHealth =
            accountStatusList.updateStatusValue(HP, calculateHealthLoss(account.level, difficultyMultiplier.value))
        if (currentHealth <= 0)
            accountStatusList.downgradeCharacter(account)
    }

    private suspend fun List<AccountStatusDTO>.downgradeCharacter(account: AccountDTO) {
        val newLevel: Long = accountService.levelDownAccount(account.username, account.level)

        updateStatusValue(HP, calculateMaxHealth(newLevel))
        updateStatusValue(EXP, 0)
        updateStatusValue(GOLD, calculateGoldLost(single { it.statusName == GOLD.dbName }.value))
    }

    private suspend fun List<AccountStatusDTO>.updateStatusValue(
        accountStatusValue: AccountStatusValue,
        valueDelta: Long
    ): Long {
        val newValue: Long
        single { it.statusName == accountStatusValue.dbName }.let {
            newValue = it.value + valueDelta
            accountStatusRepository.updateStatus(
                it.username,
                it.statusName,
                newValue
            )
        }
        return newValue
    }
}
