package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import com.example.splitter.data.repository.SplitterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed class ActivityItem {
    abstract val timestamp: Long

    data class ExpenseActivity(
        val expense: ExpenseEntity,
        val paidBy: User?,
        val groupName: String?,
        override val timestamp: Long = expense.dateTimestamp
    ) : ActivityItem()

    data class SettlementActivity(
        val settlement: SettlementEntity,
        val payer: User?,
        val payee: User?,
        val groupName: String?,
        override val timestamp: Long = settlement.dateTimestamp
    ) : ActivityItem()
}

class ActivityViewModel(private val repository: SplitterRepository) : ViewModel() {

    val activityItemsFlow: StateFlow<List<ActivityItem>> = combine(
        repository.allExpensesFlow,
        repository.allSettlementsFlow,
        repository.allUsersFlow,
        repository.allGroupsFlow
    ) { expenses, settlements, users, groups ->
        val userMap = users.associateBy { it.id }
        val groupMap = groups.associateBy { it.id }

        val expenseActivities = expenses.map { expense ->
            ActivityItem.ExpenseActivity(
                expense = expense,
                paidBy = userMap[expense.paidByUserId],
                groupName = expense.groupId?.let { groupMap[it]?.name }
            )
        }

        val settlementActivities = settlements.map { settlement ->
            ActivityItem.SettlementActivity(
                settlement = settlement,
                payer = userMap[settlement.payerId],
                payee = userMap[settlement.payeeId],
                groupName = settlement.groupId?.let { groupMap[it]?.name }
            )
        }

        (expenseActivities + settlementActivities).sortedByDescending { it.timestamp }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
