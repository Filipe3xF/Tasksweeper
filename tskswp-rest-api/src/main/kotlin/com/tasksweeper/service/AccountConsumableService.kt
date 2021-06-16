package com.tasksweeper.service

import com.tasksweeper.entities.ConsumableDTO
import com.tasksweeper.repository.AccountConsumableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountConsumableService : KoinComponent {
    private val accountConsumableRepository: AccountConsumableRepository by inject()

    suspend fun addItem(username: String, consumable: ConsumableDTO) {
        if (accountConsumableRepository.increaseQuantity(username, consumable.name) <= 0)
            accountConsumableRepository.insertAccountConsumable(username, consumable.name, 1)
    }
}
