# CityFix

A modern Android application designed to facilitate communication between citizens and the local administration. CityFix is a "Smart City" solution that allows users to quickly report urban infrastructure issues (e.g., potholes, broken streetlights) using real-time geolocation and photographic evidence. 

The application features a role-based system, providing a dedicated dashboard for administrators to manage and update the status of incoming reports, while keeping citizens informed through automated updates.

### ⚙️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel)
* **Backend as a Service (BaaS):** Google Firebase
  * *Authentication:* Secure role-based login (Citizen/Admin)
  * *Database:* Cloud Firestore (NoSQL real-time database)
  * *Storage:* Firebase Storage (for uploading image evidence)
  * *Notifications:* Firebase Cloud Messaging (FCM)
* **Location Services:** Google Maps Platform SDK (Fused Location)
* **Image Loading:** Coil

### ✨ Key Features
* Secure authentication with distinct Citizen and Admin roles.
* Interactive map integration for precise incident geolocation.
* Camera/Gallery integration for attaching visual evidence.
* Real-time dashboard for authorities to track and update issue statuses (Pending, In Progress, Resolved).
