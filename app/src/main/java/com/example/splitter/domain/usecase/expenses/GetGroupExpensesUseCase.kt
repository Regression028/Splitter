package com.example.splitter.domain.usecase.expenses

import com.example.splitter.domain.model.DomainExpense
import com.example.splitter.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupExpensesUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(groupId: String): Flow<List<DomainExpense>> {
        return expenseRepository.getExpensesForGroupFlow(groupId)
    }
}
