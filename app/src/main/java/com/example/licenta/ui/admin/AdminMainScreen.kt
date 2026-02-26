package com.example.licenta.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.licenta.data.Report
import com.example.licenta.data.ReportStatus
import com.example.licenta.viewmodel.AdminReportsViewModel
import com.example.licenta.viewmodel.ReportsState
import java.text.SimpleDateFormat
import java.util.Locale

private val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    adminViewModel: AdminReportsViewModel = viewModel()
) {
    val reportsState by adminViewModel.reportsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Panoul de Administrare (Toate Sesizările)") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            when (val state = reportsState) {
                is ReportsState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ReportsState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Eroare la încărcarea datelor: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is ReportsState.Success -> {
                    if (state.reports.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nu există sesizări trimise.")
                        }
                    } else {
                        // Afișează lista tuturor rapoartelor
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(state.reports) { report ->
                                AdminReportCard(
                                    report = report,
                                    onStatusChange = adminViewModel::updateReportStatus // Trimite funcția de actualizare
                                )
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
fun AdminReportCard(report: Report, onStatusChange: (reportId: String, newStatus: String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { /* TODO: Navigare la vizualizarea hărții */ }) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "${report.reportName} (ID: ${report.id.take(6)})", style = MaterialTheme.typography.titleLarge)
            Text(text = "De la: ${report.userId.take(8)}...", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            val statusColor = when (report.status) {
                ReportStatus.RESOLVED -> MaterialTheme.colorScheme.primary
                ReportStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            }
            Text(
                text = "STATUS CURENT: ${report.status}",
                style = MaterialTheme.typography.labelLarge.copy(color = statusColor)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Actualizează:")

                Button(
                    onClick = { onStatusChange(report.id, ReportStatus.IN_PROGRESS) },
                    enabled = report.status != ReportStatus.IN_PROGRESS
                ) {
                    Text("În Lucru")
                }

                Button(
                    onClick = { onStatusChange(report.id, ReportStatus.RESOLVED) },
                    enabled = report.status != ReportStatus.RESOLVED
                ) {
                    Text("Rezolvat")
                }
            }
        }
    }
}