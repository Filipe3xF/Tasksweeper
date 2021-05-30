package com.tasksweeper.service

import com.tasksweeper.entities.AccountDTO
import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.entities.AccountStatusValue
import com.tasksweeper.entities.AccountStatusValue.*
import com.tasksweeper.entities.DifficultyMultiplier
import com.tasksweeper.repository.AccountStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

class AccountStatusService : KoinComponent {
    private val accountStatusRepository: AccountStatusRepository by inject()
    private val accountService: AccountService by inject()

    private val maxGold = 999999999L

    private fun calculateExperienceGain(level: Long, difficulty: Int) =
        (10 + level.toDouble().pow(1.45) * difficulty).toLong()

    private fun calculateGoldGain(level: Long, difficulty: Int) = 10 + level * difficulty

    private fun calculateMaximumExperience(level: Long) = 50 + round(level.toDouble().pow(2.0) / 2).toLong()

    private fun calculateMaximumHealth(level: Long) = 100 + round(level.toDouble().pow(2.1) / 2).toLong()

    suspend fun insertInitialStatus(accountUsername: String): List<AccountStatusDTO?> {
        val list = mutableListOf<AccountStatusDTO?>()
        for (status in AccountStatusValue.values()) {
            list.add(accountStatusRepository.insertAccountStatus(accountUsername, status.dbName, status.initialValue))
        }
        return list
    }

    suspend fun reward(account: AccountDTO, difficultyMultiplier: DifficultyMultiplier) {
        increaseAccountExperience(account.username, account.level, difficultyMultiplier.value)
        increaseAccountGold(account.username, account.level, difficultyMultiplier.value)
    }

    private suspend fun increaseAccountGold(
        accountUsername: String,
        accountLevel: Long,
        difficultyMultiplier: Int
    ) = accountStatusRepository.selectAccountStatusByName(accountUsername, GOLD.dbName).let {
        if (it.value < maxGold)
            it.updateStatusValue(
                minOf(maxGold, it.value + calculateGoldGain(accountLevel, difficultyMultiplier))
            )
    }

    private suspend fun increaseAccountExperience(
        accountUsername: String,
        accountLevel: Long,
        difficultyMultiplier: Int
    ) = accountStatusRepository.selectAccountStatusByName(accountUsername, EXP.dbName).let {
        val rewardExp = it.value + calculateExperienceGain(accountLevel, difficultyMultiplier)
        val maxExp = calculateMaximumExperience(accountLevel)
        if (rewardExp >= maxExp) {
            accountLevelUp(accountUsername)
            refillAccountHealth(accountUsername, accountLevel + 1)
            it.updateStatusValue(abs(maxExp - rewardExp))
        } else {
            it.updateStatusValue(rewardExp)
        }
    }

    private suspend fun refillAccountHealth(
        accountUsername: String,
        accountLevel: Long
    ) = accountStatusRepository.updateStatus(
        accountUsername,
        HP.dbName,
        calculateMaximumHealth(accountLevel)
    )

    private suspend fun accountLevelUp(accountUsername: String) = accountService.levelUp(accountUsername)

    private suspend fun AccountStatusDTO.updateStatusValue(
        value: Long
    ) = accountStatusRepository.updateStatus(
        username,
        statusName,
        value
    )

    private suspend fun List<AccountStatusDTO>.updateStatusValue(
        accountStatusValue: AccountStatusValue,
        valueDelta: Long
    ) =
        single { it.statusName == accountStatusValue.dbName }.let {
            accountStatusRepository.updateStatus(
                it.username,
                it.statusName,
                it.value + valueDelta
            )
        }
}
