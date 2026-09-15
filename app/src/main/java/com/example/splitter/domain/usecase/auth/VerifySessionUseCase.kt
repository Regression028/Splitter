package com.example.splitter.domain.usecase.auth

import com.example.splitter.domain.model.DomainUser
import com.example.splitter.domain.repository.AuthRepository
import javax.inject.Inject

class VerifySessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<DomainUser> {
        return authRepository.verifySession()
    }
}
