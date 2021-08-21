package com.tasksweeper.service

import com.tasksweeper.entities.ConsumableStatusDTO
import com.tasksweeper.repository.ConsumableStatusRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConsumableStatusService : KoinComponent {

    private val consumableStatusRepository: ConsumableStatusRepository by inject()

    suspend fun getConsumableStatus(consumableId: Long) : ConsumableStatusDTO {
        return consumableStatusRepository.selectConsumableStatus(consumableId)
    }

}