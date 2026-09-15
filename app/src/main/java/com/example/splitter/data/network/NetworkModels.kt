package com.example.splitter.data.network

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    val name: String? = null,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: NetworkUser? = null,
    val token: String? = null,
    val data: AuthData? = null,
    val error: ApiErrorData? = null
) {
    fun extractUser(): NetworkUser? = user ?: data?.user
    fun extractToken(): String? = token ?: data?.token
    fun getErrorMessage(): String = error?.message ?: message ?: "Authentication failed"
}

data class AuthData(
    val user: NetworkUser?,
    val token: String?
)

data class ApiErrorData(
    val code: String?,
    val message: String?
)

data class NetworkUser(
    @SerializedName("_id", alternate = ["id"])
    val id: String,
    val name: String,
    val email: String,
    val avatarColorHex: String? = "#10B981"
)

data class UsersSearchResponse(
    val success: Boolean,
    val users: List<NetworkUser>? = null,
    val data: List<NetworkUser>? = null
) {
    fun getUsersList(): List<NetworkUser> = users ?: data ?: emptyList()
}

data class GroupCreateRequest(
    val name: String,
    val description: String?,
    val category: String?,
    val memberIds: List<String>
)

data class NetworkGroup(
    @SerializedName("_id", alternate = ["id"])
    val id: String,
    val name: String,
    val description: String?,
    val category: String?,
    val members: List<NetworkUser>?
)

data class GroupsResponse(
    val success: Boolean,
    val groups: List<NetworkGroup>? = null,
    val data: List<NetworkGroup>? = null
) {
    fun getGroupsList(): List<NetworkGroup> = groups ?: data ?: emptyList()
}

data class ExpenseSplitRequest(
    val user: String,
    val amountOwed: Double
)

data class ExpenseCreateRequest(
    val title: String,
    val amount: Double,
    val paidBy: String,
    val groupId: String?,
    val category: String?,
    val splitType: String?,
    val splits: List<ExpenseSplitRequest>
)

data class SettlementCreateRequest(
    val payerId: String,
    val payeeId: String,
    val groupId: String?,
    val amount: Double,
    val note: String?
)
