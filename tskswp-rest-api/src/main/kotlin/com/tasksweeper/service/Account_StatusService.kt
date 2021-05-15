package com.tasksweeper.service

import com.tasksweeper.entities.Account_StatusDTO
import com.tasksweeper.repository.Account_StatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class Account_StatusService : KoinComponent{

    private val account_statusRepository: Account_StatusRepository by inject()

    private val HP: Pair<String, Int> = Pair("Health", 5)
    private val GOLD: Pair<String, Int> = Pair("Gold", 0)
    private val EXP: Pair<String, Int> = Pair("Experience", 0)

    private val statusAndValue: List<Pair<String, Int>> = listOf(HP, GOLD, EXP)


    suspend fun insertInitialStatus(accountUsername : String) : List<Account_StatusDTO?>{
        val list = mutableListOf<Account_StatusDTO?>()
        for (status in statusAndValue) {
            list.add(account_statusRepository.insertAccount_Status(accountUsername, status.first, status.second))
        }
        return list
    }



}