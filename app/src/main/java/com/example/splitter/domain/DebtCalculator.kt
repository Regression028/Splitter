package com.example.splitter.domain

import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import kotlin.math.abs
import kotlin.math.min

data class UserBalance(
    val userId: String,
    val userName: String,
    val netBalance: Double // Positive = owed money, Negative = owes money
)

data class SimplifiedDebt(
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val amount: Double
)

data class GroupSummary(
    val group: GroupEntity,
    val totalSpending: Double,
    val userNetBalance: Double,
    val userBalances: List<UserBalance>,
    val simplifiedDebts: List<SimplifiedDebt>
)

data class FriendBalanceSummary(
    val friend: User,
    val netBalance: Double, // Positive = friend owes you, Negative = you owe friend
    val simplifiedDebts: List<SimplifiedDebt>
)

data class OverallBalanceSummary(
    val totalOwedToYou: Double,
    val totalYouOwe: Double,
    val netTotal: Double,
    val allSimplifiedDebts: List<SimplifiedDebt>
)

object DebtCalculator {

    /**
     * Calculates net balance for every user based on expenses, splits, and settlements.
     */
    fun calculateNetBalances(
        usersMap: Map<String, User>,
        expenses: List<ExpenseEntity>,
        splits: List<ExpenseSplitEntity>,
        settlements: List<SettlementEntity>
    ): Map<String, Double> {
        val netBalances = usersMap.keys.associateWith { 0.0 }.toMutableMap()

        // 1. Process Expenses
        val splitsByExpense = splits.groupBy { it.expenseId }
        for (expense in expenses) {
            val paidBy = expense.paidByUserId
            val expenseSplits = splitsByExpense[expense.id] ?: emptyList()

            // Credit the payer for total amount
            netBalances[paidBy] = (netBalances[paidBy] ?: 0.0) + expense.amount

            // Debit each member for their share
            for (split in expenseSplits) {
                netBalances[split.userId] = (netBalances[split.userId] ?: 0.0) - split.amountOwed
            }
        }

        // 2. Process Settlements
        for (settlement in settlements) {
            // Payer gave money, so their net balance increases (debt decreases)
            netBalances[settlement.payerId] = (netBalances[settlement.payerId] ?: 0.0) + settlement.amount

            // Payee received money, so their net balance decreases (credit decreases)
            netBalances[settlement.payeeId] = (netBalances[settlement.payeeId] ?: 0.0) - settlement.amount
        }

        return netBalances
    }

    /**
     * Min-Cash-Flow Algorithm for Debt Simplification.
     * Minimizes total number of transactions needed to settle all debts.
     */
    fun simplifyDebts(
        netBalances: Map<String, Double>,
        usersMap: Map<String, User>
    ): List<SimplifiedDebt> {
        val debtors = mutableListOf<Pair<String, Double>>()   // (userId, amountOwed [positive])
        val creditors = mutableListOf<Pair<String, Double>>() // (userId, amountIsOwed [positive])

        val epsilon = 0.01 // Ignore tiny floating point differences

        for ((userId, balance) in netBalances) {
            if (balance < -epsilon) {
                debtors.add(userId to abs(balance))
            } else if (balance > epsilon) {
                creditors.add(userId to balance)
            }
        }

        val result = mutableListOf<SimplifiedDebt>()

        var debtorIndex = 0
        var creditorIndex = 0

        val debtorsList = debtors.map { it.first to it.second }.toMutableList()
        val creditorsList = creditors.map { it.first to it.second }.toMutableList()

        while (debtorIndex < debtorsList.size && creditorIndex < creditorsList.size) {
            val (debtorId, debtAmount) = debtorsList[debtorIndex]
            val (creditorId, creditAmount) = creditorsList[creditorIndex]

            val minAmount = min(debtAmount, creditAmount)
            val roundedAmount = Math.round(minAmount * 100.0) / 100.0

            if (roundedAmount > 0.0) {
                val debtorName = usersMap[debtorId]?.name ?: "Unknown"
                val creditorName = usersMap[creditorId]?.name ?: "Unknown"

                result.add(
                    SimplifiedDebt(

                        fromUserId = debtorId,
                        fromUserName = debtorName,
                        toUserId = creditorId,
                        toUserName = creditorName,
                        amount = roundedAmount
                    )
                )
            }

            debtorsList[debtorIndex] = debtorId to (debtAmount - minAmount)
            creditorsList[creditorIndex] = creditorId to (creditAmount - minAmount)

            if (debtorsList[debtorIndex].second < epsilon) {
                debtorIndex++
            }
            if (creditorsList[creditorIndex].second < epsilon) {
                creditorIndex++
            }
        }

        return result
    }

    /**
     * Calculates summary metrics for a specific group.
     */
    fun calculateGroupSummary(
        group: GroupEntity,
        members: List<User>,
        expenses: List<ExpenseEntity>,
        splits: List<ExpenseSplitEntity>,
        settlements: List<SettlementEntity>,
        currentUserId: String
    ): GroupSummary {
        val usersMap = members.associateBy { it.id }
        val netBalances = calculateNetBalances(usersMap, expenses, splits, settlements)

        val totalSpending = expenses.sumOf { it.amount }
        val userNetBalance = netBalances[currentUserId] ?: 0.0

        val userBalances = members.map { user ->
            UserBalance(
                userId = user.id,
                userName = user.name,
                netBalance = netBalances[user.id] ?: 0.0
            )
        }

        val simplifiedDebts = simplifyDebts(netBalances, usersMap)

        return GroupSummary(
            group = group,
            totalSpending = totalSpending,
            userNetBalance = userNetBalance,
            userBalances = userBalances,
            simplifiedDebts = simplifiedDebts
        )
    }

    /**
     * Calculates overall balance across all groups and friends for the current user.
     */
    fun calculateOverallBalanceSummary(
        allUsers: List<User>,
        allExpenses: List<ExpenseEntity>,
        allSplits: List<ExpenseSplitEntity>,
        allSettlements: List<SettlementEntity>,
        currentUserId: String
    ): OverallBalanceSummary {
        val usersMap = allUsers.associateBy { it.id }
        val netBalances = calculateNetBalances(usersMap, allExpenses, allSplits, allSettlements)
        val simplifiedDebts = simplifyDebts(netBalances, usersMap)

        var totalOwedToYou = 0.0
        var totalYouOwe = 0.0

        for (debt in simplifiedDebts) {
            if (debt.toUserId == currentUserId) {
                totalOwedToYou += debt.amount
            } else if (debt.fromUserId == currentUserId) {
                totalYouOwe += debt.amount
            }
        }

        val netTotal = netBalances[currentUserId] ?: 0.0

        return OverallBalanceSummary(
            totalOwedToYou = totalOwedToYou,
            totalYouOwe = totalYouOwe,
            netTotal = netTotal,
            allSimplifiedDebts = simplifiedDebts
        )
    }

    /**
     * Calculates direct balance summary between current user and a single friend.
     */
    fun calculateFriendBalanceSummary(
        friend: User,
        allExpenses: List<ExpenseEntity>,
        allSplits: List<ExpenseSplitEntity>,
        allSettlements: List<SettlementEntity>,
        currentUserId: String
    ): FriendBalanceSummary {
        val usersMap = mapOf(currentUserId to User(currentUserId, "You", "", ""), friend.id to friend)

        // Filter expenses and settlements that only involve both current user and this friend
        val splitsByExpense = allSplits.groupBy { it.expenseId }

        val relevantExpenses = allExpenses.filter { expense ->
            val expenseSplits = splitsByExpense[expense.id] ?: emptyList()
            val splitUserIds = expenseSplits.map { it.userId }.toSet()
            (expense.paidByUserId == currentUserId && splitUserIds.contains(friend.id)) ||
                    (expense.paidByUserId == friend.id && splitUserIds.contains(currentUserId))
        }

        val relevantSplits = allSplits.filter { split ->
            relevantExpenses.any { it.id == split.expenseId } &&
                    (split.userId == currentUserId || split.userId == friend.id)
        }

        val relevantSettlements = allSettlements.filter {
            (it.payerId == currentUserId && it.payeeId == friend.id) ||
                    (it.payerId == friend.id && it.payeeId == currentUserId)
        }

        val netBalances = calculateNetBalances(usersMap, relevantExpenses, relevantSplits, relevantSettlements)
        // Net balance from current user's POV:
        val userPOVBalance = netBalances[currentUserId] ?: 0.0

        val simplifiedDebts = simplifyDebts(netBalances, usersMap)

        return FriendBalanceSummary(
            friend = friend,
            netBalance = userPOVBalance,
            simplifiedDebts = simplifiedDebts
        )
    }
}
