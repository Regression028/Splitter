package com.example.splitter.domain.usecase.groups

import com.example.splitter.domain.repository.GroupRepository
import javax.inject.Inject

class AddGroupMemberUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: String, email: String): Result<Unit> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid user email address"))
        }
        return groupRepository.addGroupMember(groupId, email)
    }
}
