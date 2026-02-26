package com.example.licenta.ui.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenMainScreen(
    onNavigateToSelection: () -> Unit,
    onNavigateToMyReports: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Meniul Cetățeanului") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = onNavigateToSelection,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text("Raportează o Problemă Nouă")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onNavigateToMyReports,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
            ) {
                Text("Vizualizează Sesizările Mele")
            }
        }
    }
}