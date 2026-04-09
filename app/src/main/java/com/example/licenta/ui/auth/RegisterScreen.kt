package com.example.licenta.ui.auth // Verifică să ai pachetul corect

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.licenta.R
import com.example.licenta.data.User
import com.example.licenta.viewmodel.AuthState
import com.example.licenta.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onSuccessNavigation: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.road),
            contentDescription = "Fundal Oraș",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Creare Cont",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        onClick = { authViewModel.signUp(email, password) },
                        enabled = authState != AuthState.Loading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (authState == AuthState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Înregistrare")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ai deja cont?", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Autentifică-te", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = authState) {
                is AuthState.Failure -> {
                    Text(text = "Eroare: ${state.error}", color = MaterialTheme.colorScheme.error)
                    DisposableEffect(Unit) { onDispose { authViewModel.resetState() } }
                }
                is AuthState.Success -> {
                    Text(text = "SUCCES: ${state.message}", color = Color.Green)
                    LaunchedEffect(Unit) { onSuccessNavigation(state.user) }
                }
                else -> Unit
            }
        }
    }
}