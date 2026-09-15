package com.example.splitter.data.repository

import com.example.splitter.data.dao.UserDao
import com.example.splitter.data.model.User
import com.example.splitter.data.network.AuthRequest
import com.example.splitter.data.network.SessionManager
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.domain.model.DomainUser
import com.example.splitter.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: SplitterApiService,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : AuthRepository {

    private val _currentUserState = MutableStateFlow<DomainUser?>(fetchCachedUser())
    override val currentUserFlow: Flow<DomainUser?> = _currentUserState.asStateFlow()

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    override fun getAuthToken(): String? = sessionManager.fetchAuthToken()

    override fun getCurrentUser(): DomainUser? = _currentUserState.value

    private fun fetchCachedUser(): DomainUser? {
        if (!sessionManager.isLoggedIn()) return null
        return DomainUser(
            id = sessionManager.getUserId(),
            name = sessionManager.getUserName(),
            email = sessionManager.getUserEmail(),
            avatarColorHex = sessionManager.getAvatarColor()
        )
    }

    override suspend fun login(email: String, password: String): Result<DomainUser> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(AuthRequest(email = email, password = password))
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val token = body.extractToken() ?: return@withContext Result.failure(Exception("Token missing in server response"))
                val netUser = body.extractUser() ?: return@withContext Result.failure(Exception("User data missing in server response"))

                sessionManager.saveAuthToken(token)
                sessionManager.saveUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")

                // Cache in Room
                userDao.insertUser(User(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981"))

                val domainUser = DomainUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")
                _currentUserState.value = domainUser
                Result.success(domainUser)
            } else {
                val errMsg = response.body()?.getErrorMessage() ?: "Invalid email or password"
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Connection error. Could not reach server."))
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<DomainUser> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(AuthRequest(name = name, email = email, password = password))
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val token = body.extractToken() ?: return@withContext Result.failure(Exception("Token missing in server response"))
                val netUser = body.extractUser() ?: return@withContext Result.failure(Exception("User data missing in server response"))

                sessionManager.saveAuthToken(token)
                sessionManager.saveUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")

                // Cache in Room
                userDao.insertUser(User(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981"))

                val domainUser = DomainUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")
                _currentUserState.value = domainUser
                Result.success(domainUser)
            } else {
                val errMsg = response.body()?.getErrorMessage() ?: "Registration failed"
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Connection error. Could not reach server."))
        }
    }

    override suspend fun verifySession(): Result<DomainUser> = withContext(Dispatchers.IO) {
        if (!isLoggedIn()) {
            return@withContext Result.failure(Exception("No active session"))
        }

        try {
            val response = apiService.getMe()
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val netUser = body.extractUser() ?: return@withContext Result.failure(Exception("Session invalid"))

                sessionManager.saveUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")
                val domainUser = DomainUser(netUser.id, netUser.name, netUser.email, netUser.avatarColorHex ?: "#10B981")
                _currentUserState.value = domainUser
                Result.success(domainUser)
            } else {
                logout()
                Result.failure(Exception("Session expired"))
            }
        } catch (e: Exception) {
            // Keep offline user if network is temporarily down
            val cached = fetchCachedUser()
            if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Password change network logic
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        sessionManager.logout()
        _currentUserState.value = null
    }
}
