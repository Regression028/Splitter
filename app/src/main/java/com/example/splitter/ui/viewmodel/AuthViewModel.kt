package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.domain.model.DomainUser
import com.example.splitter.domain.repository.AuthRepository
import com.example.splitter.domain.usecase.auth.LoginUseCase
import com.example.splitter.domain.usecase.auth.LogoutUseCase
import com.example.splitter.domain.usecase.auth.RegisterUseCase
import com.example.splitter.domain.usecase.auth.VerifySessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifySessionUseCase: VerifySessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<DomainUser?>(authRepository.getCurrentUser())
    val currentUser: StateFlow<DomainUser?> = _currentUser.asStateFlow()

    val currentUserEmail: String
        get() = authRepository.getCurrentUser()?.email ?: "you@example.com"

    val currentUserName: String
        get() = authRepository.getCurrentUser()?.name ?: "You"

    init {
        verifySession()
    }

    fun verifySession() {
        if (!authRepository.isLoggedIn()) return
        viewModelScope.launch {
            val result = verifySessionUseCase()
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _isLoggedIn.value = true
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _isLoggedIn.value = true
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Invalid email or password")
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = registerUseCase(name, email, password)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _isLoggedIn.value = true
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun loginOfflineDemo() {
        viewModelScope.launch {
            authRepository.login("you@example.com", "password")
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _currentUser.value = null
            _isLoggedIn.value = false
        }
    }
}
