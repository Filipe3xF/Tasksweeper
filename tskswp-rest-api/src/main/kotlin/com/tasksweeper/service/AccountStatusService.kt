package com.tasksweeper.service

import com.tasksweeper.entities.AccountStatusDTO
import com.tasksweeper.repository.AccountStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountStatusService : KoinComponent {
    private val accountStatusRepository: AccountStatusRepository by inject()
    private val HP: Pair<String, Int> = Pair("Health", 5)
    private val GOLD: Pair<String, Int> = Pair("Gold", 0)
    private val EXP: Pair<String, Int> = Pair("Experience", 0)
    private val statusAndValue: List<Pair<String, Int>> = listOf(HP, GOLD, EXP)

    suspend fun insertInitialStatus(accountUsername: String): List<AccountStatusDTO?> {
        val list = mutableListOf<AccountStatusDTO?>()
        for (status in statusAndValue) {
            list.add(accountStatusRepository.insertAccountStatus(accountUsername, status.first, status.second))
        }
        return list
    }
}