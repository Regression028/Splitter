package com.example.splitter.domain.usecase.expenses

import com.example.splitter.domain.model.DomainExpense
import com.example.splitter.domain.model.DomainExpenseParticipant
import com.example.splitter.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(
        groupId: String?,
        title: String,
        totalAmountPaise: Long,
        paidByUserId: String,
        category: String,
        splitType: String,
        participants: List<DomainExpenseParticipant>
    ): Result<DomainExpense> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Expense title is required"))
        }
        if (totalAmountPaise <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than ₹0.00"))
        }
        if (participants.isEmpty()) {
            return Result.failure(IllegalArgumentException("At least 1 participant is required"))
        }

        if (splitType == "EXACT") {
            val sumPaise = participants.sumOf { it.amountOwedPaise }
            if (sumPaise != totalAmountPaise) {
                val sumRupees = sumPaise / 100.0
                val totalRupees = totalAmountPaise / 100.0
                return Result.failure(IllegalArgumentException("Exact splits sum (₹${String.format("%.2f", sumRupees)}) does not match total expense amount (₹${String.format("%.2f", totalRupees)})"))
            }
        } else if (splitType == "PERCENTAGE") {
            val sumPercentage = participants.sumOf { it.percentage ?: 0.0 }
            if (Math.abs(sumPercentage - 100.0) > 0.01) {
                return Result.failure(IllegalArgumentException("Percentage split sum (${String.format("%.1f", sumPercentage)}%) must equal exactly 100%"))
            }
        }

        return expenseRepository.addExpense(
            groupId = groupId,
            title = title,
            totalAmountPaise = totalAmountPaise,
            paidByUserId = paidByUserId,
            category = category,
            splitType = splitType,
            participants = participants
        )
    }
}
