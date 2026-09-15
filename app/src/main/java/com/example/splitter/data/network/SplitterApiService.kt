package com.example.splitter.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SplitterApiService {

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<AuthResponse>

    @GET("users/search")
    suspend fun searchUsers(@Query("query") query: String): Response<UsersSearchResponse>

    @POST("groups")
    suspend fun createGroup(@Body request: GroupCreateRequest): Response<Map<String, Any>>

    @GET("groups")
    suspend fun getGroups(): Response<GroupsResponse>

    @POST("expenses")
    suspend fun createExpense(@Body request: ExpenseCreateRequest): Response<Map<String, Any>>

    @POST("settlements")
    suspend fun createSettlement(@Body request: SettlementCreateRequest): Response<Map<String, Any>>
}
