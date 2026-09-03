package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.User
import com.example.splitter.data.repository.SplitterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: SplitterRepository) : ViewModel() {

    val currentUserId = repository.CURRENT_USER_ID

    val allGroups: StateFlow<List<GroupEntity>> = repository.allGroupsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allUsers: StateFlow<List<User>> = repository.allUsersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addExpense(
        groupId: String?,
        title: String,
        amount: Double,
        paidByUserId: String,
        category: String,
        splits: Map<String, Double>
    ) {
        viewModelScope.launch {
            repository.addExpense(
                groupId = if (groupId.isNull_OR_Blank()) null else groupId,
                title = title,
                amount = amount,
                paidByUserId = paidByUserId,
                category = category,
                splits = splits
            )
        }
    }

    fun recordSettlement(
        groupId: String?,
        payerId: String,
        payeeId: String,
        amount: Double,
        note: String
    ) {
        viewModelScope.launch {
            repository.recordSettlement(
                groupId = if (groupId.isNull_OR_Blank()) null else groupId,
                payerId = payerId,
                payeeId = payeeId,
                amount = amount,
                note = note
            )
        }
    }

    private fun String?.isNull_OR_Blank(): Boolean = this == null || this.isBlank()
}
