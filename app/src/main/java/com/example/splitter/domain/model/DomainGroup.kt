package com.example.splitter.domain.model

data class DomainGroup(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val myRole: String = "MEMBER",
    val memberCount: Int = 1
)
