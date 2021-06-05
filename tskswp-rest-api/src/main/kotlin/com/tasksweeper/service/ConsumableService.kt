package com.tasksweeper.service

import com.tasksweeper.repository.ConsumableRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ConsumableService : KoinComponent {
    private val accountConsumableService: AccountConsumableService by inject()
    private val accountStatusService: AccountStatusService by inject()
    private val consumableRepository: ConsumableRepository by inject()

    suspend fun obtainItem(username: String, consumableId: Long): Any {
        val consumable = consumableRepository.selectConsumable(consumableId)
        accountStatusService.purchaseItem(username, consumable)
        accountConsumableService.addItem(username, consumable)
        return consumable
    }
}