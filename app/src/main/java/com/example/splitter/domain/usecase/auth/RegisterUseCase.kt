package com.example.splitter.domain.usecase.auth

import com.example.splitter.domain.model.DomainUser
import com.example.splitter.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<DomainUser> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name is required"))
        }
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        return authRepository.register(name, email, password)
    }
}
