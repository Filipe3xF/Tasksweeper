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

    private fun calculateHealthLoss(level: Long, difficulty: Int) =
        (-1) * (20 + ((level.toDouble().pow(1.8)) / difficulty)).toLong()

    private fun calculateNewGoldAfterPunish(gold: Long) = (gold - (gold * 0.10)).toLong()

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

    suspend fun punish(account: AccountDTO, difficultyMultiplier: DifficultyMultiplier) {
        takeDamage(account, difficultyMultiplier)
    }

    private suspend fun takeDamage(account: AccountDTO, difficultyMultiplier: DifficultyMultiplier) {
        val currentHealth = accountStatusRepository.selectAccountStatusByName(account.username, HP.dbName)
        val newHealth = currentHealth.value + calculateHealthLoss(account.level, difficultyMultiplier.value)

        if (newHealth <= 0)
            downgradeCharacter(account)
        else
            currentHealth.updateStatusValue(newHealth)
    }

    private suspend fun downgradeCharacter(account: AccountDTO) {
        var newLevel = account.level
        val username = account.username

        if (account.level >= 2) {
            accountLevelDown(username)
            newLevel--
        }

        refillAccountHealth(username, newLevel)
        resetAccountExperience(username)
        decreaseAccountGold(username)
    }

    private suspend fun decreaseAccountGold(accountUsername: String) =
        accountStatusRepository.selectAccountStatusByName(accountUsername, GOLD.dbName).let {
            var newGold = calculateNewGoldAfterPunish(it.value)
            if (newGold < 0)
                newGold = 0
            it.updateStatusValue(newGold)
        }

    private suspend fun resetAccountExperience(accountUsername: String) =
        accountStatusRepository.selectAccountStatusByName(accountUsername, EXP.dbName).updateStatusValue(0)

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

    private suspend fun accountLevelDown(accountUsername: String) = accountService.levelDown(accountUsername)

    private suspend fun AccountStatusDTO.updateStatusValue(
        value: Long
    ) = accountStatusRepository.updateStatus(
        username,
        statusName,
        value
    )
}
