package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import com.example.splitter.data.repository.SplitterRepository
import com.example.splitter.domain.DebtCalculator
import com.example.splitter.domain.GroupSummary
import com.example.splitter.domain.OverallBalanceSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(private val repository: SplitterRepository) : ViewModel() {

    val currentUserId = repository.CURRENT_USER_ID

    val overallBalanceSummaryFlow: StateFlow<OverallBalanceSummary> = combine(
        repository.allUsersFlow,
        repository.allExpensesFlow,
        repository.allExpenseSplitsFlow,
        repository.allSettlementsFlow
    ) { users, expenses, splits, settlements ->
        DebtCalculator.calculateOverallBalanceSummary(
            allUsers = users,
            allExpenses = expenses,
            allSplits = splits,
            allSettlements = settlements,
            currentUserId = currentUserId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverallBalanceSummary(0.0, 0.0, 0.0, emptyList())
    )

    val groupSummariesFlow: StateFlow<List<GroupSummary>> = combine(
        repository.allGroupsFlow,
        repository.allUsersFlow,
        repository.allExpensesFlow,
        repository.allExpenseSplitsFlow,
        repository.allSettlementsFlow
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val groups = flows[0] as List<GroupEntity>
        @Suppress("UNCHECKED_CAST")
        val expenses = flows[2] as List<ExpenseEntity>
        @Suppress("UNCHECKED_CAST")
        val splits = flows[3] as List<ExpenseSplitEntity>
        @Suppress("UNCHECKED_CAST")
        val settlements = flows[4] as List<SettlementEntity>

        groups.map { group ->
            val members = repository.getGroupMembers(group.id)
            val groupExpenses = expenses.filter { it.groupId == group.id }
            val groupExpenseIds = groupExpenses.map { it.id }.toSet()
            val groupSplits = splits.filter { groupExpenseIds.contains(it.expenseId) }
            val groupSettlements = settlements.filter { it.groupId == group.id }

            DebtCalculator.calculateGroupSummary(
                group = group,
                members = members,
                expenses = groupExpenses,
                splits = groupSplits,
                settlements = groupSettlements,
                currentUserId = currentUserId
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createGroup(name: String, description: String, category: String, selectedMemberIds: List<String>) {
        viewModelScope.launch {
            repository.createGroup(name, description, category, selectedMemberIds)
        }
    }
}
