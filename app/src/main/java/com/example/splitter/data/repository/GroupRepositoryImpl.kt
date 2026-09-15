package com.example.splitter.data.repository

import com.example.splitter.data.dao.GroupDao
import com.example.splitter.data.model.GroupEntity
import com.example.splitter.data.network.GroupCreateRequest
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.domain.model.DomainGroup
import com.example.splitter.domain.repository.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val apiService: SplitterApiService
) : GroupRepository {

    override fun getGroupsFlow(): Flow<List<DomainGroup>> {
        return groupDao.getAllGroupsFlow().map { entities ->
            entities.map { entity ->
                DomainGroup(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    category = entity.category
                )
            }
        }
    }

    override suspend fun fetchGroups(): Result<List<DomainGroup>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGroups()
            if (response.isSuccessful && response.body()?.success == true) {
                val netGroups = response.body()?.getGroupsList() ?: emptyList()
                val domainGroups = netGroups.map { g ->
                    val entity = GroupEntity(
                        id = g.id,
                        name = g.name,
                        description = g.description ?: "",
                        category = g.category ?: "Trip"
                    )
                    groupDao.insertGroup(entity)

                    DomainGroup(
                        id = g.id,
                        name = g.name,
                        description = g.description ?: "",
                        category = g.category ?: "Trip",
                        memberCount = g.members?.size ?: 1
                    )
                }
                Result.success(domainGroups)
            } else {
                Result.failure(Exception("Failed to fetch groups from network"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createGroup(
        name: String,
        description: String,
        category: String,
        memberEmails: List<String>
    ): Result<DomainGroup> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createGroup(
                GroupCreateRequest(
                    name = name,
                    description = description,
                    category = category,
                    memberIds = emptyList()
                )
            )

            val groupEntity = GroupEntity(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                description = description,
                category = category
            )
            groupDao.insertGroup(groupEntity)

            val domainGroup = DomainGroup(
                id = groupEntity.id,
                name = name,
                description = description,
                category = category
            )
            Result.success(domainGroup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addGroupMember(groupId: String, email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGroup(groupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
