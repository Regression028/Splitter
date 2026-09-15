package com.example.splitter.domain.usecase.balances

import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import com.example.splitter.domain.DebtCalculator
import com.example.splitter.domain.GroupSummary
import javax.inject.Inject

class CalculateGroupBalancesUseCase @Inject constructor(
    private val simplifyDebtsUseCase: SimplifyDebtsUseCase
) {
    operator fun invoke(
        group: GroupEntity,
        members: List<User>,
        expenses: List<ExpenseEntity>,
        splits: List<ExpenseSplitEntity>,
        settlements: List<SettlementEntity>,
        currentUserId: String
    ): GroupSummary {
        return DebtCalculator.calculateGroupSummary(
            group = group,
            members = members,
            expenses = expenses,
            splits = splits,
            settlements = settlements,
            currentUserId = currentUserId
        )
    }
}
