package com.example.splitter.domain.model

data class DomainExpenseParticipant(
    val userId: String,
    val userName: String = "",
    val amountOwedPaise: Long,
    val percentage: Double? = null
)

data class DomainExpense(
    val id: String,
    val groupId: String?,
    val title: String,
    val totalAmountPaise: Long,
    val currency: String = "INR",
    val paidByUserId: String,
    val paidByName: String = "",
    val category: String = "Food",
    val splitType: String = "EQUAL",
    val participants: List<DomainExpenseParticipant>,
    val dateTimestamp: Long = System.currentTimeMillis()
) {
    val totalAmountRupees: Double
        get() = totalAmountPaise / 100.0
}
