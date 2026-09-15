package com.example.splitter.domain

import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DebtCalculatorTest {

    private lateinit var userAlice: User
    private lateinit var userBob: User
    private lateinit var userCharlie: User
    private lateinit var usersMap: Map<String, User>

    @Before
    fun setUp() {
        userAlice = User("user_alice", "Alice", "alice@example.com", "#10B981")
        userBob = User("user_bob", "Bob", "bob@example.com", "#3B82F6")
        userCharlie = User("user_charlie", "Charlie", "charlie@example.com", "#F59E0B")

        usersMap = mapOf(
            userAlice.id to userAlice,
            userBob.id to userBob,
            userCharlie.id to userCharlie
        )
    }

    @Test
    fun calculateNetBalances_singleEqualExpense_calculatesAccurately() {
        // Alice pays ₹900 for dinner split equally between Alice, Bob, and Charlie (₹300 each)
        val expense = ExpenseEntity(
            id = "exp_1",
            groupId = "group_1",
            title = "Dinner",
            amount = 900.0,
            paidByUserId = userAlice.id,
            category = "Food",
            splitType = "EQUAL"
        )

        val splits = listOf(
            ExpenseSplitEntity("s1", "exp_1", userAlice.id, 300.0),
            ExpenseSplitEntity("s2", "exp_1", userBob.id, 300.0),
            ExpenseSplitEntity("s3", "exp_1", userCharlie.id, 300.0)
        )

        val balances = DebtCalculator.calculateNetBalances(usersMap, listOf(expense), splits, emptyList())

        assertEquals(600.0, balances[userAlice.id]!!, 0.01)   // Alice paid 900, share 300 -> net +600
        assertEquals(-300.0, balances[userBob.id]!!, 0.01)    // Bob paid 0, share 300 -> net -300
        assertEquals(-300.0, balances[userCharlie.id]!!, 0.01) // Charlie paid 0, share 300 -> net -300
    }

    @Test
    fun simplifyDebts_chainedDebts_simplifiesDirectly() {
        // Chained Debt: Bob owes Alice ₹500, Alice owes Charlie ₹500
        // Net Balances: Bob = -500, Alice = 0, Charlie = +500
        val netBalances = mapOf(
            userBob.id to -500.0,
            userAlice.id to 0.0,
            userCharlie.id to 500.0
        )

        val simplifiedDebts = DebtCalculator.simplifyDebts(netBalances, usersMap)

        assertEquals(1, simplifiedDebts.size)
        val debt = simplifiedDebts[0]
        assertEquals(userBob.id, debt.fromUserId)
        assertEquals(userCharlie.id, debt.toUserId)
        assertEquals(500.0, debt.amount, 0.01)
    }

    @Test
    fun calculateNetBalances_withSettlementPayment_reducesDebt() {
        // Alice paid ₹900 for dinner (Bob owes ₹300, Charlie owes ₹300)
        val expense = ExpenseEntity("exp_1", "group_1", "Dinner", 900.0, userAlice.id, "Food")
        val splits = listOf(
            ExpenseSplitEntity("s1", "exp_1", userAlice.id, 300.0),
            ExpenseSplitEntity("s2", "exp_1", userBob.id, 300.0),
            ExpenseSplitEntity("s3", "exp_1", userCharlie.id, 300.0)
        )

        // Bob pays Alice ₹300 settlement
        val settlement = SettlementEntity("set_1", "group_1", userBob.id, userAlice.id, 300.0, "GPay")

        val balances = DebtCalculator.calculateNetBalances(usersMap, listOf(expense), splits, listOf(settlement))

        assertEquals(300.0, balances[userAlice.id]!!, 0.01)   // Alice now owed only 300
        assertEquals(0.0, balances[userBob.id]!!, 0.01)      // Bob is now fully settled (0.0)
        assertEquals(-300.0, balances[userCharlie.id]!!, 0.01) // Charlie still owes 300
    }

    @Test
    fun simplifyDebts_fullySettledGroup_returnsEmptyList() {
        val netBalances = mapOf(
            userAlice.id to 0.0,
            userBob.id to 0.0,
            userCharlie.id to 0.0
        )

        val simplifiedDebts = DebtCalculator.simplifyDebts(netBalances, usersMap)

        assertTrue(simplifiedDebts.isEmpty())
    }
}
