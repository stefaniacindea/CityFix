package com.example.licenta.ui.citizen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.licenta.viewmodel.ReportState
import com.example.licenta.viewmodel.ReportViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun ReportDetailScreen(
    reportName: String,
    onBack: () -> Unit,
    onSuccessNavigate: () -> Unit,
    reportViewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val sibiuLocation = LatLng(45.7983, 24.1256)

    var markerPosition by remember { mutableStateOf(sibiuLocation) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sibiuLocation, 12f)
    }

    var description by rememberSaveable { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val reportState by reportViewModel.reportState.collectAsState()

    LaunchedEffect(reportState) {
        if (reportState is ReportState.Success) {
            android.widget.Toast.makeText(context, "Sesizarea a fost trimisă cu succes!", android.widget.Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(300)
            reportViewModel.resetState()
            onSuccessNavigate()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

    val scrollState = rememberScrollState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            markerPosition = currentLatLng
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                        }
                    }
                } catch (e: SecurityException) { e.printStackTrace() }
            }
        }
    )

    fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    markerPosition = currentLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f) // Mutăm harta
                }
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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
                Button(
                    onClick = { fetchCurrentLocation() },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Locația mea")
                    Spacer(Modifier.width(8.dp))
                    Text("Folosește locația mea curentă")
                }

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
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Icon(Icons.Filled.Image, contentDescription = "Atașează poză")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (reportState is ReportState.Failure) {
                    Text(
                        text = (reportState as ReportState.Failure).error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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