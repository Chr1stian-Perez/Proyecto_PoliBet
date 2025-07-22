package com.epn.polibet.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val fullName: String = "",
    val balance: Double = 1000.0, // Balance simulado inicial
    val createdAt: Long = System.currentTimeMillis()
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val username: String,
    val fullName: String,
    val password: String,
    val confirmPassword: String
)
