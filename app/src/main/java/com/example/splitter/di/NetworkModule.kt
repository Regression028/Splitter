package com.example.splitter.di

import com.example.splitter.data.network.ApiClient
import com.example.splitter.data.network.SessionManager
import com.example.splitter.data.network.SplitterApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSplitterApiService(sessionManager: SessionManager): SplitterApiService {
        return ApiClient.createService(sessionManager)
    }
}
