package com.example.splitter.domain.usecase.groups

import com.example.splitter.domain.repository.GroupRepository
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: String): Result<Unit> {
        return groupRepository.deleteGroup(groupId)
    }
}
