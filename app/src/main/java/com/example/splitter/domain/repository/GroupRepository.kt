package com.example.splitter.domain.repository

import com.example.splitter.domain.model.DomainGroup
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getGroupsFlow(): Flow<List<DomainGroup>>
    suspend fun fetchGroups(): Result<List<DomainGroup>>
    suspend fun createGroup(name: String, description: String, category: String, memberEmails: List<String>): Result<DomainGroup>
    suspend fun addGroupMember(groupId: String, email: String): Result<Unit>
    suspend fun deleteGroup(groupId: String): Result<Unit>
}
