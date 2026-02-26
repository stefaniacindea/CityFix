@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.licenta.ui.citizen
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ListItem
import androidx.compose.material3.Divider
@Composable
fun CategorySelectionScreen(
    onNavigateToDetails: (reportName: String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
           TopAppBar(
                title = { Text("Alege Tipul de Raportare") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(ReportCategories.categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider()

                        category.items.forEach { item ->
                            ListItem(
                                headlineContent = { Text(item.name) },
                                supportingContent = { Text(item.description) },
                                modifier = Modifier
                                    .clickable {
                                        onNavigateToDetails(item.name)
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}