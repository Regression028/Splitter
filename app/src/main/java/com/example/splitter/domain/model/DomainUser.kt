package com.example.splitter.domain.model

data class DomainUser(
    val id: String,
    val name: String,
    val email: String,
    val avatarColorHex: String = "#10B981",
    val defaultCurrency: String = "INR"
)
