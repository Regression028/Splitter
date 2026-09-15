package com.example.splitter.di

import com.example.splitter.data.dao.ExpenseDao
import com.example.splitter.data.dao.GroupDao
import com.example.splitter.data.dao.SettlementDao
import com.example.splitter.data.dao.UserDao
import com.example.splitter.data.db.SplitterDatabase
import com.example.splitter.data.network.SessionManager
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.data.repository.AuthRepositoryImpl
import com.example.splitter.data.repository.ExpenseRepositoryImpl
import com.example.splitter.data.repository.GroupRepositoryImpl
import com.example.splitter.data.repository.SettlementRepositoryImpl
import com.example.splitter.data.repository.SplitterRepository
import com.example.splitter.domain.repository.AuthRepository
import com.example.splitter.domain.repository.ExpenseRepository
import com.example.splitter.domain.repository.GroupRepository
import com.example.splitter.domain.repository.SettlementRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSplitterRepository(database: SplitterDatabase): SplitterRepository {
        return SplitterRepository(database)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: SplitterApiService,
        sessionManager: SessionManager,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, sessionManager, userDao)
    }

    @Provides
    @Singleton
    fun provideGroupRepository(
        groupDao: GroupDao,
        apiService: SplitterApiService
    ): GroupRepository {
        return GroupRepositoryImpl(groupDao, apiService)
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
        apiService: SplitterApiService
    ): ExpenseRepository {
        return ExpenseRepositoryImpl(expenseDao, apiService)
    }

    @Provides
    @Singleton
    fun provideSettlementRepository(
        settlementDao: SettlementDao,
        apiService: SplitterApiService
    ): SettlementRepository {
        return SettlementRepositoryImpl(settlementDao, apiService)
    }
}
