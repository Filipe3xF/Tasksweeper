package com.tasksweeper.service

import com.tasksweeper.entities.ConsumableDTO
import com.tasksweeper.repository.AccountConsumableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountConsumableService : KoinComponent {
    private val accountConsumableRepository: AccountConsumableRepository by inject()

    suspend fun addItem(username: String, consumable: ConsumableDTO) {
        if (accountConsumableRepository.increaseQuantity(username, consumable.id) <= 0)
            accountConsumableRepository.insertAccountConsumable(username, consumable.id)
    }

    suspend fun getAccountConsumables(username: String) =
        accountConsumableRepository.selectAccountConsumables(username)
}
