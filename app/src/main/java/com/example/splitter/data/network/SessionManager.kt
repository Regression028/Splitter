package com.example.splitter.data.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("splitter_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_AVATAR_COLOR = "avatar_color"
        private const val KEY_SERVER_IP = "server_ip"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUser(id: String, name: String, email: String, avatarColorHex: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_AVATAR_COLOR, avatarColorHex)
            apply()
        }
    }

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "user_you") ?: "user_you"
    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "You") ?: "You"
    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "you@example.com") ?: "you@example.com"
    fun getAvatarColor(): String = prefs.getString(KEY_AVATAR_COLOR, "#10B981") ?: "#10B981"

    fun saveServerIp(ip: String) {
        prefs.edit().putString(KEY_SERVER_IP, ip).apply()
    }

    fun getServerUrl(): String {
        val ip = prefs.getString(KEY_SERVER_IP, null)
        return if (!ip.isNullOrBlank()) {
            if (ip.startsWith("http://") || ip.startsWith("https://")) {
                if (ip.endsWith("/")) ip else "$ip/"
            } else {
                "http://$ip/api/"
            }
        } else {
            // Default: localhost:8080 (works with 'adb reverse tcp:8080 tcp:8080' on physical phone & emulator)
            "http://10.0.2.2:8080/api/"
        }
    }

    fun isLoggedIn(): Boolean {
        return fetchAuthToken() != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
