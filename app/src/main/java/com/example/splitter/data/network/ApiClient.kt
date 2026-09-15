package com.example.splitter.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 10.0.2.2 connects Android Emulator to host machine's localhost:8080
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    fun createService(sessionManager: SessionManager): SplitterApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()

                val token = sessionManager.fetchAuthToken()
                if (!token.isNull_OR_Blank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

        val customUrl = sessionManager.getServerUrl()
        val baseUrl = if (customUrl.contains("10.0.2.2") || customUrl.contains("localhost")) {
            com.example.splitter.BuildConfig.BASE_URL
        } else {
            customUrl
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SplitterApiService::class.java)
    }

    private fun String?.isNull_OR_Blank(): Boolean = this == null || this.isBlank()
}
