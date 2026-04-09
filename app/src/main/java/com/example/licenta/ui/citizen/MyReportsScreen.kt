package com.example.licenta.ui.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.licenta.data.Report
import com.example.licenta.data.ReportStatus
import com.example.licenta.viewmodel.ReportsListViewModel
import com.example.licenta.viewmodel.ReportsState
import java.text.SimpleDateFormat
import java.util.Locale

private val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsScreen(
    reportsListViewModel: ReportsListViewModel = viewModel()
) {
    val reportsState by reportsListViewModel.reportsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Istoricul Sesizărilor Mele") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            when (val state = reportsState) {
                is ReportsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ReportsState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Eroare la încărcarea datelor: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is ReportsState.Success -> {
                    if (state.reports.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text("Nu ai trimis încă nicio sesizare.")
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(state.reports) { report ->
                                ReportCard(report = report)
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: Report) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = report.reportName,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            if (!report.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = "Poză atașată de utilizator",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Trimis la: ${dateFormat.format(report.timestamp.toDate())}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))

            val statusColor = when (report.status) {
                ReportStatus.RESOLVED -> MaterialTheme.colorScheme.primary
                ReportStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            }
            Text(
                text = "STATUS: ${report.status}",
                style = MaterialTheme.typography.labelLarge.copy(color = statusColor)
            )
        }
    }
}