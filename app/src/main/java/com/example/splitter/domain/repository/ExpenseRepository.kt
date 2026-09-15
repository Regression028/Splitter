package com.example.splitter.domain.repository

import com.example.splitter.domain.model.DomainExpense
import com.example.splitter.domain.model.DomainExpenseParticipant
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getExpensesForGroupFlow(groupId: String): Flow<List<DomainExpense>>
    fun getAllExpensesFlow(): Flow<List<DomainExpense>>
    suspend fun addExpense(
        groupId: String?,
        title: String,
        totalAmountPaise: Long,
        paidByUserId: String,
        category: String,
        splitType: String,
        participants: List<DomainExpenseParticipant>
    ): Result<DomainExpense>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
}
