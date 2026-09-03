package com.example.splitter.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.splitter.data.dao.ExpenseDao
import com.example.splitter.data.dao.GroupDao
import com.example.splitter.data.dao.SettlementDao
import com.example.splitter.data.dao.UserDao
import com.example.splitter.data.model.ExpenseEntity
import com.example.splitter.data.model.ExpenseSplitEntity
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.model.GroupMemberEntity
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.model.User

@Database(
    entities = [
        User::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class,
        SettlementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SplitterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settlementDao(): SettlementDao

    companion object {
        @Volatile
        private var INSTANCE: SplitterDatabase? = null

        fun getDatabase(context: Context): SplitterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SplitterDatabase::class.java,
                    "splitter_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
