package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitter.data.repository.SplitterRepository

class ViewModelFactory(private val repository: SplitterRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GroupViewModel::class.java) -> GroupViewModel(repository) as T
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> ExpenseViewModel(repository) as T
            modelClass.isAssignableFrom(FriendViewModel::class.java) -> FriendViewModel(repository) as T
            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> ActivityViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
