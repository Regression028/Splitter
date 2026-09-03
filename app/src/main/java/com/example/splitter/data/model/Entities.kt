package com.example.splitter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarColorHex: String
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String, // e.g. "Trip", "Home", "Couples", "Other"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "group_members", primaryKeys = ["groupId", "userId"])
data class GroupMemberEntity(
    val groupId: String,
    val userId: String
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String?, // Nullable for direct 1-on-1 expenses
    val title: String,
    val amount: Double,
    val paidByUserId: String,
    val category: String, // "Food", "Transport", "Utilities", "Entertainment", "Shopping", "Other"
    val splitType: String = "EQUAL", // "EQUAL", "EXACT"
    val dateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "expense_splits")
data class ExpenseSplitEntity(
    @PrimaryKey val id: String,
    val expenseId: String,
    val userId: String,
    val amountOwed: Double
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val groupId: String?,
    val payerId: String,
    val payeeId: String,
    val amount: Double,
    val note: String = "Settled up",
    val dateTimestamp: Long = System.currentTimeMillis()
)
