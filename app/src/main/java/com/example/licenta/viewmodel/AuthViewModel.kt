package com.example.licenta.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.licenta.data.User
import com.example.licenta.data.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.messaging.FirebaseMessaging

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

        if (email.trim().endsWith(ADMIN_DOMAIN, ignoreCase = true)) {
            _authState.value = AuthState.Failure("Nu se pot crea conturi noi cu domeniu de administrator.")
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
        val roleToAssign = UserRole.CITIZEN
        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
            val proaspatToken = if (tokenTask.isSuccessful) tokenTask.result else null

            val userMap = hashMapOf<String, Any>(
                "email" to email,
                "role" to roleToAssign
            )

            if (proaspatToken != null) {
                userMap["fcmToken"] = proaspatToken
            }

            firestore.collection("users")
                .document(uid)
                .set(userMap)
                .addOnSuccessListener {
                    val newUser = User(uid = uid, email = email, role = roleToAssign)
                    _authState.value = AuthState.Success(newUser, "Înregistrare reușită!")
                }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Failure("Înregistrare reușită, dar salvarea profilului a eșuat.")
                }
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
            val proaspatToken = if (tokenTask.isSuccessful) tokenTask.result else null

            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role") ?: UserRole.CITIZEN

                    if (proaspatToken != null) {
                        firestore.collection("users").document(uid)
                            .update("fcmToken", proaspatToken)
                    }

                    val loggedInUser = User(uid = uid, email = email, role = role)
                    _authState.value = AuthState.Success(loggedInUser, "Login reușit!")
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Failure("Eroare la citirea profilului.")
                }
        }
    }
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}