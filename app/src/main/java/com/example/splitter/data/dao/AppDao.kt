package com.example.splitter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.GroupMemberEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
}

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(members: List<GroupMemberEntity>)

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroupsFlow(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): GroupEntity?

    @Query("SELECT u.* FROM users u INNER JOIN group_members gm ON u.id = gm.userId WHERE gm.groupId = :groupId")
    fun getGroupMembersFlow(groupId: String): Flow<List<User>>

    @Query("SELECT u.* FROM users u INNER JOIN group_members gm ON u.id = gm.userId WHERE gm.groupId = :groupId")
    suspend fun getGroupMembers(groupId: String): List<User>

    @Query("SELECT userId FROM group_members WHERE groupId = :groupId")
    suspend fun getGroupMemberIds(groupId: String): List<String>
}

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseSplits(splits: List<ExpenseSplitEntity>)

    @Query("SELECT * FROM expenses ORDER BY dateTimestamp DESC")
    fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY dateTimestamp DESC")
    fun getExpensesForGroupFlow(groupId: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE groupId = :groupId ORDER BY dateTimestamp DESC")
    suspend fun getExpensesForGroup(groupId: String): List<ExpenseEntity>

    @Query("SELECT * FROM expenses ORDER BY dateTimestamp DESC")
    suspend fun getAllExpenses(): List<ExpenseEntity>

    @Query("SELECT * FROM expense_splits WHERE expenseId = :expenseId")
    suspend fun getSplitsForExpense(expenseId: String): List<ExpenseSplitEntity>

    @Query("SELECT * FROM expense_splits")
    suspend fun getAllExpenseSplits(): List<ExpenseSplitEntity>

    @Query("SELECT * FROM expense_splits")
    fun getAllExpenseSplitsFlow(): Flow<List<ExpenseSplitEntity>>

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: String)

    @Query("DELETE FROM expense_splits WHERE expenseId = :expenseId")
    suspend fun deleteExpenseSplits(expenseId: String)
}

@Dao
interface SettlementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Query("SELECT * FROM settlements ORDER BY dateTimestamp DESC")
    fun getAllSettlementsFlow(): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY dateTimestamp DESC")
    fun getSettlementsForGroupFlow(groupId: String): Flow<List<SettlementEntity>>

    @Query("SELECT * FROM settlements")
    suspend fun getAllSettlements(): List<SettlementEntity>

    @Query("SELECT * FROM settlements WHERE groupId = :groupId")
    suspend fun getSettlementsForGroup(groupId: String): List<SettlementEntity>
}
