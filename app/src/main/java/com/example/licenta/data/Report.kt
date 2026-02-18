package com.example.licenta.data

import com.google.firebase.Timestamp

data class LocationData(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

data class Report(
    val id: String = "",
    val userId: String = "",
    val reportName: String = "",
    val description: String = "",
    val location: LocationData = LocationData(),
    val imageUrl: String? = null,
    val timestamp: Timestamp = Timestamp.now(),
    val status: String = "Pending"
)

object ReportStatus {
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val RESOLVED = "Resolved"
}