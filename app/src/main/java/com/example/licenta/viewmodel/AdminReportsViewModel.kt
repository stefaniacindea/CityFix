package com.example.licenta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.licenta.data.Report
import com.example.licenta.data.LocationData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.example.licenta.viewmodel.ReportsStat


class AdminReportsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _reportsState = MutableStateFlow<ReportsState>(ReportsState.Loading)
    val reportsState: StateFlow<ReportsState> = _reportsState

    init {
        if (auth.currentUser != null) {
            viewModelScope.launch {
                getReportsStream().collect {
                    _reportsState.value = ReportsState.Success(it)
                }
            }
        } else {
            _reportsState.value = ReportsState.Error("Autentificare eșuată.")
        }
    }

    private fun getReportsStream() = callbackFlow {
        val reportsRef = firestore.collection("reports")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)

        val subscription = reportsRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val reports = snapshot.documents.map { document ->
                    val locationObject = document.get("location") as? Map<String, Any>
                    val locationData = if (locationObject != null) {
                        LocationData(
                            lat = (locationObject["lat"] as? Number)?.toDouble() ?: 0.0,
                            lon = (locationObject["lon"] as? Number)?.toDouble() ?: 0.0
                        )
                    } else LocationData()

                    Report(
                        id = document.id,
                        userId = document.getString("userId") ?: "",
                        reportName = document.getString("reportName") ?: "Necunoscut",
                        description = document.getString("description") ?: "",
                        location = locationData,
                        imageUrl = document.getString("imageUrl"),
                        timestamp = document.getTimestamp("timestamp") ?: Timestamp.now(),
                        status = document.getString("status") ?: "Pending"
                    )
                }
                trySend(reports)
            }
        }
        awaitClose { subscription.remove() }
    }

    fun updateReportStatus(reportId: String, newStatus: String) {
        firestore.collection("reports").document(reportId)
            .update("status", newStatus)
            .addOnFailureListener {
                println("Eroare la actualizarea statusului: ${it.message}")
            }
    }
}