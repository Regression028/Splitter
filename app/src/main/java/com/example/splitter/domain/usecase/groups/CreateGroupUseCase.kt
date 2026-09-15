package com.example.splitter.domain.usecase.groups

import com.example.splitter.domain.model.DomainGroup
import com.example.splitter.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String,
        category: String,
        memberEmails: List<String>
    ): Result<DomainGroup> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Group name is required"))
        }
        return groupRepository.createGroup(name, description, category, memberEmails)
    }
}
