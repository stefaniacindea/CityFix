package com.example.licenta.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.licenta.data.User
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.licenta.viewmodel.AuthState
import com.example.licenta.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onSuccessNavigation: (User) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Raportare Probleme", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Parolă") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.signIn(email, password) },
            enabled = authState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (authState == AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Login")
            }
        }

        OutlinedButton(
            onClick = { authViewModel.signUp(email, password) },
            enabled = authState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Înregistrare")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = authState) {
            is AuthState.Failure -> {
                Text(text = "Eroare: ${state.error}", color = MaterialTheme.colorScheme.error)
                DisposableEffect(Unit) { onDispose { authViewModel.resetState() } }
            }
            is AuthState.Success -> {
                Text(text = "SUCCES: ${state.message}", color = MaterialTheme.colorScheme.primary)
                LaunchedEffect(Unit) { onSuccessNavigation(state.user) }
            }
            else -> Unit
        }
    }
}