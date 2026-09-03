package com.example.splitter.data.repository

import com.example.splitter.data.db.SplitterDatabase
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.GroupMemberEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SplitterRepository(private val db: SplitterDatabase) {

    val CURRENT_USER_ID = "user_you"

    val allUsersFlow: Flow<List<User>> = db.userDao().getAllUsersFlow()
    val allGroupsFlow: Flow<List<GroupEntity>> = db.groupDao().getAllGroupsFlow()
    val allExpensesFlow: Flow<List<ExpenseEntity>> = db.expenseDao().getAllExpensesFlow()
    val allSettlementsFlow: Flow<List<SettlementEntity>> = db.settlementDao().getAllSettlementsFlow()

    suspend fun getAllUsers(): List<User> = db.userDao().getAllUsers()
    suspend fun getUserById(id: String): User? = db.userDao().getUserById(id)

    suspend fun getGroupById(groupId: String): GroupEntity? = db.groupDao().getGroupById(groupId)
    fun getGroupMembersFlow(groupId: String): Flow<List<User>> = db.groupDao().getGroupMembersFlow(groupId)
    suspend fun getGroupMembers(groupId: String): List<User> = db.groupDao().getGroupMembers(groupId)

    fun getExpensesForGroupFlow(groupId: String): Flow<List<ExpenseEntity>> = db.expenseDao().getExpensesForGroupFlow(groupId)
    suspend fun getExpensesForGroup(groupId: String): List<ExpenseEntity> = db.expenseDao().getExpensesForGroup(groupId)
    suspend fun getAllExpenses(): List<ExpenseEntity> = db.expenseDao().getAllExpenses()
    suspend fun getSplitsForExpense(expenseId: String): List<ExpenseSplitEntity> = db.expenseDao().getSplitsForExpense(expenseId)
    suspend fun getAllExpenseSplits(): List<ExpenseSplitEntity> = db.expenseDao().getAllExpenseSplits()
    fun getAllExpenseSplitsFlow(): Flow<List<ExpenseSplitEntity>> = db.expenseDao().getAllExpenseSplitsFlow()

    fun getSettlementsForGroupFlow(groupId: String): Flow<List<SettlementEntity>> = db.settlementDao().getSettlementsForGroupFlow(groupId)
    suspend fun getSettlementsForGroup(groupId: String): List<SettlementEntity> = db.settlementDao().getSettlementsForGroup(groupId)
    suspend fun getAllSettlements(): List<SettlementEntity> = db.settlementDao().getAllSettlements()

    suspend fun createGroup(name: String, description: String, category: String, memberUserIds: List<String>): String {
        val groupId = UUID.randomUUID().toString()
        val group = GroupEntity(id = groupId, name = name, description = description, category = category)
        db.groupDao().insertGroup(group)

        val allMembers = (memberUserIds + CURRENT_USER_ID).distinct()
        val memberEntities = allMembers.map { userId ->
            GroupMemberEntity(groupId = groupId, userId = userId)
        }
        db.groupDao().insertGroupMembers(memberEntities)
        return groupId
    }

    suspend fun addExpense(
        groupId: String?,
        title: String,
        amount: Double,
        paidByUserId: String,
        category: String,
        splits: Map<String, Double> // userId -> amountOwed
    ): String {
        val expenseId = UUID.randomUUID().toString()
        val expense = ExpenseEntity(
            id = expenseId,
            groupId = groupId,
            title = title,
            amount = amount,
            paidByUserId = paidByUserId,
            category = category
        )
        db.expenseDao().insertExpense(expense)

        val splitEntities = splits.map { (userId, amountOwed) ->
            ExpenseSplitEntity(
                id = UUID.randomUUID().toString(),
                expenseId = expenseId,
                userId = userId,
                amountOwed = amountOwed
            )
        }
        db.expenseDao().insertExpenseSplits(splitEntities)
        return expenseId
    }

    suspend fun recordSettlement(
        groupId: String?,
        payerId: String,
        payeeId: String,
        amount: Double,
        note: String = "Settled payment"
    ) {
        val settlement = SettlementEntity(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            payerId = payerId,
            payeeId = payeeId,
            amount = amount,
            note = note
        )
        db.settlementDao().insertSettlement(settlement)
    }

    suspend fun addFriend(name: String, email: String) {
        val friendUser = User(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name,
            email = email,
            avatarColorHex = listOf("#4CAF50", "#2196F3", "#9C27B0", "#FF9800", "#E91E63", "#00BCD4").random()
        )
        db.userDao().insertUser(friendUser)
    }

    suspend fun ensureSampleDataPopulated() {
        val existingUsers = db.userDao().getAllUsers()
        if (existingUsers.isNotEmpty()) return // Already seeded

        // Seed Users
        val currentUser = User(CURRENT_USER_ID, "You", "you@example.com", "#10B981") // Emerald Green
        val alice = User("user_alice", "Alice Smith", "alice@example.com", "#3B82F6") // Blue
        val bob = User("user_bob", "Bob Johnson", "bob@example.com", "#F59E0B") // Amber
        val charlie = User("user_charlie", "Charlie Brown", "charlie@example.com", "#8B5CF6") // Purple
        val diana = User("user_diana", "Diana Prince", "diana@example.com", "#EC4899") // Pink

        db.userDao().insertUsers(listOf(currentUser, alice, bob, charlie, diana))

        // Seed Groups
        val g1Id = "group_goa"
        val g2Id = "group_apartment"
        val g3Id = "group_party"

        val goaGroup = GroupEntity(g1Id, "Goa Beach Trip 🏖️", "Sun, sand & seafood", "Trip")
        val apartmentGroup = GroupEntity(g2Id, "Apartment 402 🏠", "Monthly rent & utilities", "Home")
        val partyGroup = GroupEntity(g3Id, "Weekend Party 🎉", "Friday night drinks & snacks", "Entertainment")

        db.groupDao().insertGroup(goaGroup)
        db.groupDao().insertGroup(apartmentGroup)
        db.groupDao().insertGroup(partyGroup)

        // Seed Group Members
        db.groupDao().insertGroupMembers(
            listOf(
                GroupMemberEntity(g1Id, CURRENT_USER_ID),
                GroupMemberEntity(g1Id, alice.id),
                GroupMemberEntity(g1Id, bob.id),

                GroupMemberEntity(g2Id, CURRENT_USER_ID),
                GroupMemberEntity(g2Id, charlie.id),
                GroupMemberEntity(g2Id, diana.id),

                GroupMemberEntity(g3Id, CURRENT_USER_ID),
                GroupMemberEntity(g3Id, alice.id),
                GroupMemberEntity(g3Id, bob.id),
                GroupMemberEntity(g3Id, charlie.id)
            )
        )

        // Seed Expenses in Goa Trip
        addExpense(
            groupId = g1Id,
            title = "Resort Booking",
            amount = 15000.0,
            paidByUserId = CURRENT_USER_ID,
            category = "Hotels",
            splits = mapOf(CURRENT_USER_ID to 5000.0, alice.id to 5000.0, bob.id to 5000.0)
        )

        addExpense(
            groupId = g1Id,
            title = "Seafood Dinner at Shack",
            amount = 4500.0,
            paidByUserId = alice.id,
            category = "Food",
            splits = mapOf(CURRENT_USER_ID to 1500.0, alice.id to 1500.0, bob.id to 1500.0)
        )

        addExpense(
            groupId = g1Id,
            title = "Scooter Rentals",
            amount = 2400.0,
            paidByUserId = bob.id,
            category = "Transport",
            splits = mapOf(CURRENT_USER_ID to 800.0, alice.id to 800.0, bob.id to 800.0)
        )

        // Seed Expenses in Apartment 402
        addExpense(
            groupId = g2Id,
            title = "Wi-Fi & Broadband Bill",
            amount = 1200.0,
            paidByUserId = CURRENT_USER_ID,
            category = "Utilities",
            splits = mapOf(CURRENT_USER_ID to 400.0, charlie.id to 400.0, diana.id to 400.0)
        )

        addExpense(
            groupId = g2Id,
            title = "Monthly Groceries",
            amount = 3600.0,
            paidByUserId = charlie.id,
            category = "Groceries",
            splits = mapOf(CURRENT_USER_ID to 1200.0, charlie.id to 1200.0, diana.id to 1200.0)
        )

        // Seed Settlements
        recordSettlement(
            groupId = g1Id,
            payerId = alice.id,
            payeeId = CURRENT_USER_ID,
            amount = 2000.0,
            note = "Partial payment for Resort"
        )
    }
}
