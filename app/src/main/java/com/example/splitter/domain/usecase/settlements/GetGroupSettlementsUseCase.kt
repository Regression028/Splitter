package com.example.splitter.domain.usecase.settlements

import com.example.splitter.domain.model.DomainSettlement
import com.example.splitter.domain.repository.SettlementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupSettlementsUseCase @Inject constructor(
    private val settlementRepository: SettlementRepository
) {
    operator fun invoke(groupId: String): Flow<List<DomainSettlement>> {
        return settlementRepository.getSettlementsForGroupFlow(groupId)
    }
}
