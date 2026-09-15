package com.example.splitter.domain.usecase.groups

import com.example.splitter.domain.model.DomainGroup
import com.example.splitter.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    operator fun invoke(): Flow<List<DomainGroup>> {
        return groupRepository.getGroupsFlow()
    }

    suspend fun syncRemote(): Result<List<DomainGroup>> {
        return groupRepository.fetchGroups()
    }
}
