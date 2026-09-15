package com.example.splitter.domain.repository

import com.example.splitter.domain.model.DomainUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun getAuthToken(): String?
    fun getCurrentUser(): DomainUser?
    val currentUserFlow: Flow<DomainUser?>

    suspend fun login(email: String, password: String): Result<DomainUser>
    suspend fun register(name: String, email: String, password: String): Result<DomainUser>
    suspend fun verifySession(): Result<DomainUser>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun logout()
}
