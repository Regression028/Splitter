package com.example.splitter.data.repository

import com.example.splitter.data.dao.ExpenseDao
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.network.ExpenseCreateRequest
import com.example.splitter.data.network.ExpenseSplitRequest
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.domain.model.DomainExpense
import com.example.splitter.domain.model.DomainExpenseParticipant
import com.example.splitter.domain.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val apiService: SplitterApiService
) : ExpenseRepository {

    override fun getExpensesForGroupFlow(groupId: String): Flow<List<DomainExpense>> {
        return expenseDao.getExpensesForGroupFlow(groupId).map { entities ->
            entities.map { entity ->
                DomainExpense(
                    id = entity.id,
                    groupId = entity.groupId,
                    title = entity.title,
                    totalAmountPaise = Math.round(entity.amount * 100),
                    paidByUserId = entity.paidByUserId,
                    category = entity.category,
                    splitType = entity.splitType,
                    participants = emptyList()
                )
            }
        }
    }

    override fun getAllExpensesFlow(): Flow<List<DomainExpense>> {
        return expenseDao.getAllExpensesFlow().map { entities ->
            entities.map { entity ->
                DomainExpense(
                    id = entity.id,
                    groupId = entity.groupId,
                    title = entity.title,
                    totalAmountPaise = Math.round(entity.amount * 100),
                    paidByUserId = entity.paidByUserId,
                    category = entity.category,
                    splitType = entity.splitType,
                    participants = emptyList()
                )
            }
        }
    }

    override suspend fun addExpense(
        groupId: String?,
        title: String,
        totalAmountPaise: Long,
        paidByUserId: String,
        category: String,
        splitType: String,
        participants: List<DomainExpenseParticipant>
    ): Result<DomainExpense> = withContext(Dispatchers.IO) {
        try {
            val expenseId = UUID.randomUUID().toString()
            val totalAmountRupees = totalAmountPaise / 100.0

            val expenseEntity = ExpenseEntity(
                id = expenseId,
                groupId = groupId,
                title = title,
                amount = totalAmountRupees,
                paidByUserId = paidByUserId,
                category = category,
                splitType = splitType
            )
            expenseDao.insertExpense(expenseEntity)

            val splitEntities = participants.map { p ->
                ExpenseSplitEntity(
                    id = UUID.randomUUID().toString(),
                    expenseId = expenseId,
                    userId = p.userId,
                    amountOwed = p.amountOwedPaise / 100.0
                )
            }
            expenseDao.insertExpenseSplits(splitEntities)

            // Remote API Call
            if (!groupId.isNullOrBlank()) {
                apiService.createExpense(
                    ExpenseCreateRequest(
                        title = title,
                        amount = totalAmountRupees,
                        paidBy = paidByUserId,
                        groupId = groupId,
                        category = category,
                        splitType = splitType,
                        splits = participants.map { ExpenseSplitRequest(user = it.userId, amountOwed = it.amountOwedPaise / 100.0) }
                    )
                )
            }

            val domainExpense = DomainExpense(
                id = expenseId,
                groupId = groupId,
                title = title,
                totalAmountPaise = totalAmountPaise,
                paidByUserId = paidByUserId,
                category = category,
                splitType = splitType,
                participants = participants
            )
            Result.success(domainExpense)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.deleteExpense(expenseId)
            expenseDao.deleteExpenseSplits(expenseId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
