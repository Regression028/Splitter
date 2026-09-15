package com.example.splitter.domain.usecase.settlements

import com.example.splitter.domain.model.DomainSettlement
import com.example.splitter.domain.repository.SettlementRepository
import javax.inject.Inject

class RecordSettlementUseCase @Inject constructor(
    private val settlementRepository: SettlementRepository
) {
    suspend operator fun invoke(
        groupId: String?,
        payerId: String,
        payeeId: String,
        amountPaise: Long,
        note: String
    ): Result<DomainSettlement> {
        if (payerId.isBlank() || payeeId.isBlank()) {
            return Result.failure(IllegalArgumentException("Payer and payee are required"))
        }
        if (payerId == payeeId) {
            return Result.failure(IllegalArgumentException("Payer and payee cannot be the same user"))
        }
        if (amountPaise <= 0) {
            return Result.failure(IllegalArgumentException("Settlement amount must be greater than ₹0.00"))
        }

        return settlementRepository.recordSettlement(
            groupId = groupId,
            payerId = payerId,
            payeeId = payeeId,
            amountPaise = amountPaise,
            note = note.ifBlank { "Settled payment" }
        )
    }
}
