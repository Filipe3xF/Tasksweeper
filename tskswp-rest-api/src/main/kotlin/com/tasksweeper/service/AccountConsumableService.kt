package com.tasksweeper.service

import com.tasksweeper.entities.AccountConsumableDTO
import com.tasksweeper.entities.ConsumableDTO
import com.tasksweeper.entities.ConsumableStatusDTO
import com.tasksweeper.exceptions.NoConsumablesToUseException
import com.tasksweeper.repository.AccountConsumableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountConsumableService : KoinComponent {
    private val accountConsumableRepository: AccountConsumableRepository by inject()

    private val consumableStatusService: ConsumableStatusService by inject()

    private val accountService: AccountService by inject()

    private val accountStatusService: AccountStatusService by inject()

    suspend fun addItem(username: String, consumable: ConsumableDTO) {
        if (accountConsumableRepository.increaseQuantity(username, consumable.id) <= 0)
            accountConsumableRepository.insertAccountConsumable(username, consumable.id)
    }

    suspend fun useItem(username: String, consumableId: Long): AccountConsumableDTO {
        val accountConsumable: AccountConsumableDTO =
            accountConsumableRepository.selectAccountConsumable(username, consumableId)
                ?: throw NoConsumablesToUseException(username);

        if ((accountConsumable.quantity - 1) >= 1)
            accountConsumableRepository.decreaseQuantity(username, consumableId)
        else
            accountConsumableRepository.deleteAccountConsumable(username, consumableId)

        val consumableStatus: ConsumableStatusDTO = consumableStatusService.getConsumableStatus(consumableId)

        val level: Long = accountService.getAccount(username).level

        accountStatusService.affectStatusWithConsumable(username, level, consumableStatus)

        return accountConsumable.copy(quantity = accountConsumable.quantity -1)
    }
}
