package com.example.splitter.domain.repository

import com.example.splitter.domain.model.DomainSettlement
import kotlinx.coroutines.flow.Flow

interface SettlementRepository {
    fun getSettlementsForGroupFlow(groupId: String): Flow<List<DomainSettlement>>
    fun getAllSettlementsFlow(): Flow<List<DomainSettlement>>
    suspend fun recordSettlement(
        groupId: String?,
        payerId: String,
        payeeId: String,
        amountPaise: Long,
        note: String
    ): Result<DomainSettlement>
}
