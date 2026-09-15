package com.example.splitter.domain.usecase.balances

import com.example.splitter.domain.DebtCalculator
import com.example.splitter.domain.SimplifiedDebt
import com.example.splitter.domain.UserBalance
import javax.inject.Inject

class SimplifyDebtsUseCase @Inject constructor() {
    operator fun invoke(
        netBalances: Map<String, Double>,
        usersMap: Map<String, com.example.splitter.data.model.User>
    ): List<SimplifiedDebt> {
        return DebtCalculator.simplifyDebts(netBalances, usersMap)
    }
}
