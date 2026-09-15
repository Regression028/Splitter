package com.example.splitter.data.repository

import com.example.splitter.data.dao.SettlementDao
import com.example.splitter.data.model.SettlementEntity
import com.example.splitter.data.network.SettlementCreateRequest
import com.example.splitter.data.network.SplitterApiService
import com.example.splitter.domain.model.DomainSettlement
import com.example.splitter.domain.repository.SettlementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettlementRepositoryImpl @Inject constructor(
    private val settlementDao: SettlementDao,
    private val apiService: SplitterApiService
) : SettlementRepository {

    override fun getSettlementsForGroupFlow(groupId: String): Flow<List<DomainSettlement>> {
        return settlementDao.getSettlementsForGroupFlow(groupId).map { entities ->
            entities.map { entity ->
                DomainSettlement(
                    id = entity.id,
                    groupId = entity.groupId,
                    payerId = entity.payerId,
                    payeeId = entity.payeeId,
                    amountPaise = Math.round(entity.amount * 100),
                    note = entity.note,
                    dateTimestamp = entity.dateTimestamp
                )
            }
        }
    }

    override fun getAllSettlementsFlow(): Flow<List<DomainSettlement>> {
        return settlementDao.getAllSettlementsFlow().map { entities ->
            entities.map { entity ->
                DomainSettlement(
                    id = entity.id,
                    groupId = entity.groupId,
                    payerId = entity.payerId,
                    payeeId = entity.payeeId,
                    amountPaise = Math.round(entity.amount * 100),
                    note = entity.note,
                    dateTimestamp = entity.dateTimestamp
                )
            }
        }
    }

    override suspend fun recordSettlement(
        groupId: String?,
        payerId: String,
        payeeId: String,
        amountPaise: Long,
        note: String
    ): Result<DomainSettlement> = withContext(Dispatchers.IO) {
        try {
            val settlementId = UUID.randomUUID().toString()
            val amountRupees = amountPaise / 100.0

            val entity = SettlementEntity(
                id = settlementId,
                groupId = groupId,
                payerId = payerId,
                payeeId = payeeId,
                amount = amountRupees,
                note = note
            )
            settlementDao.insertSettlement(entity)

            // Remote API Call
            apiService.createSettlement(
                SettlementCreateRequest(
                    payerId = payerId,
                    payeeId = payeeId,
                    groupId = groupId,
                    amount = amountRupees,
                    note = note
                )
            )

            val domainSettlement = DomainSettlement(
                id = settlementId,
                groupId = groupId,
                payerId = payerId,
                payeeId = payeeId,
                amountPaise = amountPaise,
                note = note
            )
            Result.success(domainSettlement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
