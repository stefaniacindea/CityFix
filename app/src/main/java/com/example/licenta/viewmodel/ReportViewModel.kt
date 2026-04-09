package com.example.licenta.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    data class Success(val message: String) : ReportState()
    data class Failure(val error: String) : ReportState()
}

class ReportViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState

    fun submitReport(
        reportName: String,
        description: String,
        location: LatLng,
        imageUri: Uri?
    ) {
        _reportState.value = ReportState.Loading

        if (auth.currentUser == null) {
            _reportState.value = ReportState.Failure("Eroare: Utilizator neautentificat.")
            return
        }

        if (imageUri != null) {
            uploadImageAndSaveReport(reportName, description, location, imageUri)
        } else {
            saveReportToFirestore(reportName, description, location, null)
        }
    }

    private fun uploadImageAndSaveReport(
        reportName: String,
        description: String,
        location: LatLng,
        imageUri: Uri
    ) {
        val fileName = "reports/${auth.currentUser!!.uid}/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    saveReportToFirestore(reportName, description, location, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                _reportState.value = ReportState.Failure("Eroare la încărcarea imaginii: ${e.message}")
            }
    }

    private fun saveReportToFirestore(
        reportName: String,
        description: String,
        location: LatLng,
        imageUrl: String?
    ) {
        val reportData = hashMapOf(
            "userId" to auth.currentUser!!.uid,
            "reportName" to reportName,
            "description" to description,
            "location" to hashMapOf("lat" to location.latitude, "lon" to location.longitude),
            "imageUrl" to imageUrl,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "status" to "Pending"
        )

        firestore.collection("reports")
            .add(reportData)
            .addOnSuccessListener {
                _reportState.value = ReportState.Success("Sesizarea a fost trimisă cu succes!")
            }
            .addOnFailureListener { e ->
                _reportState.value = ReportState.Failure("Eroare la salvarea în baza de date: ${e.message}")
            }
    }
    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}