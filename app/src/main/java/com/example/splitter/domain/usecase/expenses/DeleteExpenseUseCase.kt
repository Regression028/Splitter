package com.example.splitter.domain.usecase.expenses

import com.example.splitter.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: String): Result<Unit> {
        return expenseRepository.deleteExpense(expenseId)
    }
}
