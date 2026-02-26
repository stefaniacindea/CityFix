package com.example.licenta.ui.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Image
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.licenta.viewmodel.ReportState
import com.example.licenta.viewmodel.ReportViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun ReportDetailScreen(
    reportName: String,
    onBack: () -> Unit,
    reportViewModel: ReportViewModel = viewModel()
) {
    val sibiuLocation = LatLng(45.7983, 24.1256)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sibiuLocation, 12f)
    }

    var markerPosition by remember { mutableStateOf(sibiuLocation) }

    var description by rememberSaveable { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val reportState by reportViewModel.reportState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raportează: $reportName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerPosition = latLng
                }
            ) {
                Marker(
                    state = MarkerState(position = markerPosition),
                    title = reportName,
                    snippet = "Atingeți harta pentru a muta locația"
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(scrollState)
            ) {

                Text(
                    text = "Locație selectată: ${markerPosition.latitude.toString().take(7)}, ${markerPosition.longitude.toString().take(7)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrie situația") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (imageUri == null) "Atașează o fotografie (Opțional)" else "Poză atașată: ${imageUri!!.lastPathSegment?.take(20)}...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Icon(Icons.Filled.Image, contentDescription = "Atașează poză")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))



                when (val state = reportState) {
                    is ReportState.Success -> {
                        Text(state.message, color = MaterialTheme.colorScheme.primary)
                    }
                    is ReportState.Failure -> {
                        Text(state.error, color = MaterialTheme.colorScheme.error)
                    }
                    else -> Unit
                }

                Button(
                    onClick = {
                        reportViewModel.submitReport(
                            reportName = reportName,
                            description = description,
                            location = markerPosition,
                            imageUri = imageUri
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = description.isNotBlank() && reportState != ReportState.Loading
                ) {
                    if (reportState == ReportState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Trimitere Sesizare")
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}