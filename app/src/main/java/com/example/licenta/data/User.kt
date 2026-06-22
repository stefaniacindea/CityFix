package com.example.licenta.data

data class User(
    val uid: String = "",
    val email: String? = null,
    val role: String = UserRole.CITIZEN
)

object UserRole {
    const val CITIZEN = "CITIZEN"
    const val ADMIN = "ADMIN"
}