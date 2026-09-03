package com.example.splitter

import android.app.Application
import com.example.splitter.data.db.SplitterDatabase
import com.example.splitter.data.network.ApiClient
import com.example.splitter.data.network.SessionManager
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.data.repository.SplitterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplitterApplication : Application() {

    lateinit var repository: SplitterRepository
        private set

    lateinit var sessionManager: SessionManager
        private set

    lateinit var apiService: SplitterApiService
        private set

    override fun onCreate() {
        super.onCreate()
        val database = SplitterDatabase.getDatabase(this)
        repository = SplitterRepository(database)

        sessionManager = SessionManager(this)
        apiService = ApiClient.createService(sessionManager)

        // Seed initial sample data if database is empty
        CoroutineScope(Dispatchers.IO).launch {
            repository.ensureSampleDataPopulated()
        }
    }
}
