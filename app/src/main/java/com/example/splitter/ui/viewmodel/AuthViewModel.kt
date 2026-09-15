package com.example.splitter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitter.data.network.AuthRequest
import com.example.splitter.data.network.SessionManager
import com.example.splitter.data.network.SplitterApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthViewModel(
    private val apiService: SplitterApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    val currentUserEmail: String
        get() = sessionManager.getUserEmail()

    val currentUserName: String
        get() = sessionManager.getUserName()

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.login(AuthRequest(email = email, password = password))
                }
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    body.extractToken()?.let { sessionManager.saveAuthToken(it) }
                    body.extractUser()?.let {
                        sessionManager.saveUser(it.id, it.name, it.email, it.avatarColorHex ?: "#10B981")
                    }
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    val errMsg = response.body()?.getErrorMessage() ?: "Invalid email or password"
                    onError(errMsg)
                }
            } catch (e: SocketTimeoutException) {
                onError("Server timeout. Is Node.js running on port 8080?")
            } catch (e: UnknownHostException) {
                onError("Network error. Cannot reach server.")
            } catch (e: Exception) {
                onError("Connection error: ${e.localizedMessage ?: "Could not connect to server"}")
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.register(AuthRequest(name = name, email = email, password = password))
                }
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    body.extractToken()?.let { sessionManager.saveAuthToken(it) }
                    body.extractUser()?.let {
                        sessionManager.saveUser(it.id, it.name, it.email, it.avatarColorHex ?: "#10B981")
                    }
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    val errMsg = response.body()?.getErrorMessage() ?: "Registration failed"
                    onError(errMsg)
                }
            } catch (e: SocketTimeoutException) {
                onError("Server timeout. Is Node.js running on port 8080?")
            } catch (e: UnknownHostException) {
                onError("Network error. Cannot reach server.")
            } catch (e: Exception) {
                onError("Connection error: ${e.localizedMessage ?: "Could not connect to server"}")
            }
        }
    }

    fun loginOfflineDemo() {
        sessionManager.saveAuthToken("demo_token_offline")
        sessionManager.saveUser("user_you", "You (Offline)", "you@example.com", "#10B981")
        _isLoggedIn.value = true
    }

    fun logout() {
        sessionManager.logout()
        _isLoggedIn.value = false
    }
}
