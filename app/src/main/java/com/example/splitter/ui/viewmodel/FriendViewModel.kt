package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.data.repository.SplitterRepository
import com.example.splitter.domain.DebtCalculator
import com.example.splitter.domain.FriendBalanceSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendViewModel(private val repository: SplitterRepository) : ViewModel() {

    val currentUserId = repository.CURRENT_USER_ID

    val friendsSummaryFlow: StateFlow<List<FriendBalanceSummary>> = combine(
        repository.allUsersFlow,
        repository.allExpensesFlow,
        repository.allExpenseSplitsFlow,
        repository.allSettlementsFlow
    ) { users, expenses, splits, settlements ->
        val friends = users.filter { it.id != currentUserId }
        friends.map { friend ->
            DebtCalculator.calculateFriendBalanceSummary(
                friend = friend,
                allExpenses = expenses,
                allSplits = splits,
                allSettlements = settlements,
                currentUserId = currentUserId
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addFriend(name: String, email: String) {
        viewModelScope.launch {
            repository.addFriend(name, email)
        }
    }
}
