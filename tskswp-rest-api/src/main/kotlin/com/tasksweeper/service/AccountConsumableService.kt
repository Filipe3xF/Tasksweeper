package com.tasksweeper.service

import com.tasksweeper.entities.ConsumableDTO
import com.tasksweeper.exceptions.DatabaseNotFoundException
import com.tasksweeper.repository.AccountConsumableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountConsumableService : KoinComponent {
    private val accountConsumableRepository: AccountConsumableRepository by inject()

    suspend fun addItem(username: String, consumable: ConsumableDTO) {
        try {
            accountConsumableRepository.selectAccountConsumable(username, consumable.name)
            accountConsumableRepository.increaseQuantity(username, consumable.name)
        } catch (e: DatabaseNotFoundException) {
            accountConsumableRepository.insertAccountConsumable(username, consumable.name, 1)
        }
    }
}