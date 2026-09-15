package com.example.splitter.di

import android.content.Context
import com.example.splitter.data.dao.ExpenseDao
import com.example.splitter.data.dao.GroupDao
import com.example.splitter.data.dao.SettlementDao
import com.example.splitter.data.dao.UserDao
import com.example.splitter.data.db.SplitterDatabase
import com.example.splitter.data.network.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SplitterDatabase {
        return SplitterDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: SplitterDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideGroupDao(database: SplitterDatabase): GroupDao = database.groupDao()

    @Provides
    @Singleton
    fun provideExpenseDao(database: SplitterDatabase): ExpenseDao = database.expenseDao()

    @Provides
    @Singleton
    fun provideSettlementDao(database: SplitterDatabase): SettlementDao = database.settlementDao()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}
