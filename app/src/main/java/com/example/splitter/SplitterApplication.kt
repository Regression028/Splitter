package com.example.splitter

import android.app.Application
import com.example.splitter.data.db.SplitterDatabase
import com.example.splitter.data.repository.SplitterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplitterApplication : Application() {

    lateinit var repository: SplitterRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = SplitterDatabase.getDatabase(this)
        repository = SplitterRepository(database)

        // Seed initial sample data if database is empty
        CoroutineScope(Dispatchers.IO).launch {
            repository.ensureSampleDataPopulated()
        }
    }
}
