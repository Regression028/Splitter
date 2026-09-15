package com.example.splitter.domain.model

data class DomainSettlement(
    val id: String,
    val groupId: String?,
    val payerId: String,
    val payerName: String = "",
    val payeeId: String,
    val payeeName: String = "",
    val amountPaise: Long,
    val note: String = "Settled payment",
    val dateTimestamp: Long = System.currentTimeMillis()
) {
    val amountRupees: Double
        get() = amountPaise / 100.0
}
