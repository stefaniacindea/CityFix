# CityFix 

CityFix is an Android application I developed to help citizens easily report problems in their city (like potholes, broken streetlights, or damaged benches) directly to the local administration.

Instead of going to the city hall or making phone calls, a user can just take a photo of the issue, and the app automatically grabs the exact GPS location. 

The app has a role-based system with two types of accounts:
* **Citizens:** Can send reports and see the history and status of their own submissions.
* **Administrators (City Hall):** Have a dedicated dashboard to see all incoming reports from the city and update their status ("Pending", "In Progress", or "Resolved").

###  Tech Stack Used
* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Architecture:** MVVM
* **Backend:** Google Firebase (Authentication, Firestore for database, Storage for images, and Cloud Messaging for push notifications)
* **Maps & Location:** Google Maps Platform SDK

###  Main Features
* Secure Login/Register system that automatically detects the user's role.
* Interactive Google Maps integration to pinpoint the exact location of the problem.
* Uploading photos straight from the camera or gallery.
* Real-time dashboard for admins to manage the reports.
