package com.example.licenta.data

/**
 * Modelul de date pentru un utilizator.
 */
data class User(
    val uid: String = "",
    val email: String? = null,
    // Rolul: va fi fie "CITIZEN", fie "ADMIN"
    val role: String = UserRole.CITIZEN
)

// Constante pentru a evita erorile de scriere a rolurilor
object UserRole {
    const val CITIZEN = "CITIZEN"
    const val ADMIN = "ADMIN"
}