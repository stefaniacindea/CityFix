package com.example.licenta.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.licenta.data.User
import com.example.licenta.data.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User, val message: String) : AuthState()
    data class Failure(val error: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val ADMIN_DOMAIN = "@admin.com"
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Failure("Email-ul și parola nu pot fi goale.")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        saveUserRole(it.uid, email)
                    }
                } else {
                    _authState.value = AuthState.Failure(task.exception?.message ?: "Eroare la înregistrare.")
                }
            }
    }


    private fun saveUserRole(uid: String, email: String) {

        val roleToAssign = if (email.endsWith(ADMIN_DOMAIN, ignoreCase = true)) {
            UserRole.ADMIN
        } else {
            UserRole.CITIZEN
        }

        val userMap = hashMapOf(
            "email" to email,
            "role" to roleToAssign
        )

        firestore.collection("users")
            .document(uid)
            .set(userMap)
            .addOnSuccessListener {
                val newUser = User(uid = uid, email = email, role = roleToAssign)
                _authState.value = AuthState.Success(newUser, "Înregistrare reușită!")
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Failure("Înregistrare reușită, dar salvarea rolului a eșuat.")
            }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Failure("Vă rugăm introduceți email și parolă.")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    fetchUserRole(uid, email)
                } else {
                    _authState.value = AuthState.Failure(task.exception?.message ?: "Eroare la autentificare.")
                }
            }
    }


    private fun fetchUserRole(uid: String, email: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: UserRole.CITIZEN
                val loggedInUser = User(uid = uid, email = email, role = role)

                _authState.value = AuthState.Success(loggedInUser, "Login reușit!")
            }
            .addOnFailureListener {
                _authState.value = AuthState.Failure("Login reușit, dar rolul nu a putut fi citit.")
            }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}